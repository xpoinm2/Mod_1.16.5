package com.example.examplemod.entity;

import com.example.examplemod.ModEntityTypes;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.fml.network.NetworkHooks;

import software.bernie.geckolib3.core.Animation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import software.bernie.geckolib3.core.IAnimationTickable;

public class BeaverEntity extends AnimalEntity implements IAnimatable, IAnimationTickable {

    private static final Ingredient FAVORITE_FOOD = Ingredient.of(Items.APPLE);
    private static final String ANIMATION_IDLE = "animation.beaver.idle";
    private static final String ANIMATION_WALK = "animation.beaver.walk";
    private static final String ANIMATION_SWIM = "animation.beaver.swim";
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public BeaverEntity(EntityType<? extends BeaverEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, FAVORITE_FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }

    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
        return ModEntityTypes.BEAVER.get().create(world);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FAVORITE_FOOD.test(stack);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.isInWaterOrBubble()) {
            return playAnimation(event, ANIMATION_SWIM);
        }

        if (event.isMoving() || isMovingHorizontally()) {
            return playAnimation(event, ANIMATION_WALK);
        }

        return playAnimation(event, ANIMATION_IDLE);
    }

    private boolean isMovingHorizontally() {
        final double threshold = 1.0E-4;
        final Vector3d delta = this.getDeltaMovement();
        final double horizontalSpeedSquared = delta.x * delta.x + delta.z * delta.z;
        return horizontalSpeedSquared > threshold;
    }

    private <E extends IAnimatable> PlayState playAnimation(AnimationEvent<E> event, String animationName) {
        AnimationController<E> controller = event.getController();
        Animation current = controller.getCurrentAnimation();

        if (current == null || !animationName.equals(current.animationName)) {
            controller.setAnimation(new AnimationBuilder().addAnimation(animationName, true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }


    @Override
    public net.minecraft.network.IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
