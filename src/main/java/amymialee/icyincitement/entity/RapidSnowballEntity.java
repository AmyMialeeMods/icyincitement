package amymialee.icyincitement.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class RapidSnowballEntity extends SnowballEntity {
    public RapidSnowballEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        entityHitResult.getEntity().timeUntilRegen = 0;
        super.onEntityHit(entityHitResult);
    }
}
