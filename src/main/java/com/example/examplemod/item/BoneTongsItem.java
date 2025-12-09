package com.example.examplemod.item;

import com.example.examplemod.container.BoneTongsContainer;
import com.example.examplemod.item.HotRoastedOreItem;
import com.example.examplemod.tileentity.FirepitTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoneTongsItem extends Item {
    private static final String TAG_MODE = "BoneTongsMode";

    public enum Mode {
        PICK,
        PLACE;

        public Mode next() {
            return this == PICK ? PLACE : PICK;
        }
    }

    public BoneTongsItem(Properties properties) {
        super(properties.durability(20));
    }

    public static Mode getMode(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return Mode.PICK;
        int value = tag.getInt(TAG_MODE);
        return value == Mode.PLACE.ordinal() ? Mode.PLACE : Mode.PICK;
    }

    public static Mode toggleMode(ItemStack stack) {
        Mode next = getMode(stack).next();
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt(TAG_MODE, next.ordinal());
        return next;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BoneTongsCapabilityProvider(stack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    new SimpleNamedContainerProvider(
                            (windowId, playerInventory, playerEntity) -> new BoneTongsContainer(windowId, playerInventory, held),
                            new TranslationTextComponent("container.examplemod.bone_tongs")),
                    buffer -> buffer.writeItem(held));
        }
        return ActionResult.success(held);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide) {
            return ActionResultType.PASS;
        }
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResultType.PASS;
        }
        ItemStack held = context.getItemInHand();
        if (held.getMaxDamage() - held.getDamageValue() <= 0) {
            notifyBroken(world, player, context.getClickedPos());
            return ActionResultType.FAIL;
        }
        if (!(world.getBlockEntity(context.getClickedPos()) instanceof FirepitTileEntity)) {
            return ActionResultType.PASS;
        }
        FirepitTileEntity firepit = (FirepitTileEntity) world.getBlockEntity(context.getClickedPos());
        boolean transferred = tryTransferFromFirepit(world, firepit, held, player, context.getHand(), context.getClickedPos());
        return transferred ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    private boolean tryTransferFromFirepit(World world, FirepitTileEntity firepit, ItemStack tongs, PlayerEntity player, Hand hand, BlockPos pos) {
        if (firepit == null || world == null) {
            return false;
        }
        AtomicBoolean inserted = new AtomicBoolean(false);
        tongs.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(targetHandler -> {
            outer:
            for (int slot = 0; slot < FirepitTileEntity.GRID_SLOT_COUNT; slot++) {
                ItemStack candidate = firepit.getItem(slot);
                if (candidate.isEmpty() || !(candidate.getItem() instanceof HotRoastedOreItem)) {
                    continue;
                }
                for (int targetSlot = 0; targetSlot < targetHandler.getSlots(); targetSlot++) {
                    if (!targetHandler.getStackInSlot(targetSlot).isEmpty()) {
                        continue;
                    }
                    ItemStack toInsert = ItemHandlerHelper.copyStackWithSize(candidate, 1);
                    ItemStack remainder = targetHandler.insertItem(targetSlot, toInsert, false);
                    if (remainder.isEmpty()) {
                        firepit.removeItem(slot, 1);
                        inserted.set(true);
                        break outer;
                    }
                }
            }
        });
        if (inserted.get()) {
            if (!world.isClientSide) {
                tongs.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(hand));
                world.playSound(null, pos, SoundEvents.ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 0.8f, 1f);
                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).sendParticles(ParticleTypes.LARGE_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.85, pos.getZ() + 0.5,
                            4, 0.15, 0.15, 0.15, 0.01);
                }
                firepit.setChanged();
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        }
        return inserted.get();
    }

    private void notifyBroken(World world, PlayerEntity player, BlockPos pos) {
        if (world.isClientSide) {
            return;
        }
        player.displayClientMessage(new StringTextComponent("The Bone Tongs are too worn to hold hot ore."), true);
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    6, 0.2, 0.1, 0.2, 0.005);
        }
    }
}
