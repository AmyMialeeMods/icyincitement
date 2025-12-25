package dev.amymialee.icyincitement.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class SawbladeRenderState extends EntityRenderState {
    public Quaternionf rotation = new Quaternionf().set(0, 0, 0, 1);
    public float xRot;
    public float yRot;

    public Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }
}