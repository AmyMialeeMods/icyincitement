package amymialee.icyincitement.entity;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
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
        Entity entity = entityHitResult.getEntity();
        if (FabricLoader.getInstance().isModLoaded("create")) {
            if (entity instanceof SuperGlueEntity) {
                return;
            }
        }
        entity.timeUntilRegen = 0;
        super.onEntityHit(entityHitResult);
    }
}
