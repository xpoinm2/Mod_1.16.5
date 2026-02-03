package com.example.examplemod.item;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModContainers;
import com.example.examplemod.container.BoneTongsContainer;
import com.example.examplemod.container.EnhancedDualContainer;
import com.example.examplemod.tileentity.PechugaTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BoneTongsItem extends Item {
    public BoneTongsItem(Properties properties) {
        super(properties.durability(30));
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
            return ActionResultType.FAIL;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = world.getBlockState(clickedPos);

        // Обработка кирпичных блоков печи (PechugaBlock)
        if (clickedState.getBlock() instanceof ModBlocks.PechugaBlock) {
            return handlePechugaBlockClick(world, clickedPos, clickedState, (ServerPlayerEntity) player, held);
        }

        // Обработка кирпичных блоков с футеровкой
        if (clickedState.getBlock() instanceof ModBlocks.BrickBlockWithLining) {
            return handleBrickBlockClick(world, clickedPos, (ServerPlayerEntity) player, held);
        }

        return ActionResultType.PASS;
    }

    private ActionResultType handlePechugaBlockClick(World world, BlockPos pos, BlockState state,
                                                   ServerPlayerEntity player, ItemStack tongs) {
        // Получаем координаты блока в структуре
        int x = state.getValue(ModBlocks.PechugaBlock.X);
        int y = state.getValue(ModBlocks.PechugaBlock.Y);
        int z = state.getValue(ModBlocks.PechugaBlock.Z);

        // Находим начало структуры
        BlockPos structureStart = pos.offset(-x, -y, -z);

        // Находим позицию ядра печи (центр структуры)
        BlockPos pechugaPos = structureStart.offset(2, 0, 2);

        // Проверяем, что ядро существует и активно
        if (world.getBlockState(pechugaPos).getBlock() == ModBlocks.PECHUGA_CORE_BLOCK.get()) {
            PechugaTileEntity pechuga = (PechugaTileEntity) world.getBlockEntity(pechugaPos);
            if (pechuga != null) {
                // Открываем enhanced dual GUI вместо обычного
                openEnhancedDualGUI(player, pechugaPos, tongs, false);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.FAIL;
    }

    private ActionResultType handleBrickBlockClick(World world, BlockPos pos,
                                                 ServerPlayerEntity player, ItemStack tongs) {
        // Используем существующий обработчик из PechugaStructureHandler
        // Но сначала проверяем, можем ли открыть enhanced dual GUI
        if (tryOpenEnhancedDualForBrick(world, pos, player, tongs)) {
            return ActionResultType.SUCCESS;
        }

        // Fallback на обычный обработчик
        if (com.example.examplemod.server.PechugaStructureHandler.tryOpenGui(world, pos, player)) {
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private boolean tryOpenEnhancedDualForBrick(World world, BlockPos pos, ServerPlayerEntity player, ItemStack tongs) {
        BlockPos pechugaMaster = com.example.examplemod.server.PechugaStructureHandler.findActivatedPechugaMaster(world, pos);
        if (pechugaMaster == null) {
            return false;
        }
        if (!(world.getBlockEntity(pechugaMaster) instanceof PechugaTileEntity)) {
            return false;
        }
        openEnhancedDualGUI(player, pechugaMaster, tongs, false);
        return true;
    }

    public static void openEnhancedDualGUI(ServerPlayerEntity player, BlockPos corePos, ItemStack tongs, boolean isFirepit) {
        boolean inOffhand = player.getOffhandItem() == tongs;
        NetworkHooks.openGui(player,
                new SimpleNamedContainerProvider(
                        (windowId, playerInventory, playerEntity) -> {
                            // Создаем enhanced dual контейнер
                            // Передаем данные: isFirepit, tongs, и данные для основного контейнера
                            return createEnhancedDualContainer(windowId, playerInventory, corePos, isFirepit, inOffhand);
                        },
                        new TranslationTextComponent(isFirepit ? "container.examplemod.firepit" : "container.examplemod.pechuga")),
                buffer -> {
                    // Пишем данные для контейнера
                    buffer.writeBoolean(isFirepit);
                    buffer.writeBoolean(inOffhand);
                    buffer.writeBlockPos(corePos);
                });
    }

    private static EnhancedDualContainer createEnhancedDualContainer(int windowId, PlayerInventory playerInventory,
                                                            BlockPos corePos, boolean isFirepit, boolean inOffhand) {
        // Создаем PacketBuffer с необходимыми данными
        io.netty.buffer.ByteBuf buffer = io.netty.buffer.Unpooled.buffer();
        net.minecraft.network.PacketBuffer packetBuffer = new net.minecraft.network.PacketBuffer(buffer);

        packetBuffer.writeBoolean(isFirepit);
        packetBuffer.writeBoolean(inOffhand);
        packetBuffer.writeBlockPos(corePos);

        // Создаем контейнер напрямую с правильными данными
        EnhancedDualContainer container = new EnhancedDualContainer(windowId, playerInventory, packetBuffer);
        return container;
    }
}
