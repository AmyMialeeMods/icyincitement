package amymialee.icyincitement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class IcyIncitementClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {}

    static {
        FabricModelPredicateProviderRegistry.register(IcyIncitement.SNOWBALL_SPRINKLER, new Identifier("charge"),
                (stack, world, entity, number) -> 1 - ((float) stack.getDamage() / stack.getMaxDamage()));

        FabricModelPredicateProviderRegistry.register(IcyIncitement.EMPTY_SPRINKLER, new Identifier("beta"),
                (stack, world, entity, number) -> Objects.equals(stack.getName().asString().toLowerCase(), "oldball sprinkler") ? 1 : 0);
        FabricModelPredicateProviderRegistry.register(IcyIncitement.SNOWBALL_SPRINKLER, new Identifier("beta"),
                (stack, world, entity, number) -> Objects.equals(stack.getName().asString().toLowerCase(), "oldball sprinkler") ? 1 : 0);
    }
}