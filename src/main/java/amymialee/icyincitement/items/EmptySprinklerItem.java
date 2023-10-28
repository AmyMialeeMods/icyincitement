package amymialee.icyincitement.items;

import amymialee.icyincitement.IcyIncitement;
import amymialee.icyincitement.entity.RapidSnowballEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public class EmptySprinklerItem extends RangedWeaponItem {
    public static final Predicate<ItemStack> SNOWBALLS = stack -> stack.isOf(Items.SNOWBALL);

    public EmptySprinklerItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return SNOWBALLS;
    }

    @Override
    public int getRange() {
        return 48;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack itemStack = user.getProjectileType(stack);
        if (itemStack.getCount() > 0) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.1f, 1f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

            if (!world.isClient) {
                RapidSnowballEntity snowballEntity = new RapidSnowballEntity(world, user);
                snowballEntity.setItem(Items.SNOWBALL.getDefaultStack());
                snowballEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.5f, 0f);
                world.spawnEntity(snowballEntity);
            }

            float yaw = user.getHeadYaw();
            float pitch = user.getPitch();

            float f = -MathHelper.sin(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            float g = -MathHelper.sin(pitch * ((float) Math.PI / 180));
            float h = MathHelper.cos(yaw * ((float) Math.PI / 180)) * MathHelper.cos(pitch * ((float) Math.PI / 180)) * .25f;
            Vector3f vec = new Vector3f(f, g, h);

            vec.mul(-0.175f);
            user.addVelocity(vec.x(), vec.y(), vec.z());

            if (pitch >= 30) {
                user.fallDistance -= 4;
                if (user.fallDistance < 0) {
                    user.fallDistance = 0;
                }
            }

            if (!user.getAbilities().creativeMode) itemStack.decrement(1);

            user.getItemCooldownManager().set(asItem(), 2);
            return TypedActionResult.success(stack, false);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof SnowGolemEntity) {
            ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(IcyIncitement.SNOWBALL_SPRINKLER));
            itemEntity.setPickupDelay(0);
            itemEntity.setVelocity(itemEntity.getVelocity().add(
                    (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f,
                    entity.getRandom().nextFloat() * 0.05f,
                    (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f)
            );
            entity.getWorld().spawnEntity(itemEntity);
            stack.decrement(1);
            for (int i = 0; i < 20; ++i) {
                double d = entity.getRandom().nextGaussian() * 0.02;
                double e = entity.getRandom().nextGaussian() * 0.02;
                double f = entity.getRandom().nextGaussian() * 0.02;
                entity.getWorld().addParticle(ParticleTypes.POOF, entity.getParticleX(1.0), entity.getRandomBodyY(), entity.getParticleZ(1.0), d, e, f);
            }
            entity.getWorld().playSoundFromEntity(null, itemEntity, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.6f, 1);
            entity.discard();
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(stack.getTranslationKey()+".description_1").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable(stack.getTranslationKey()+".description_2").formatted(Formatting.DARK_AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }
}