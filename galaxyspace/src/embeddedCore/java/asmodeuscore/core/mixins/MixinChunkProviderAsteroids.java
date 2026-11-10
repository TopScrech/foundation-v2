package asmodeuscore.core.mixins;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.core.configs.AsmodeusConfig;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.ChunkProviderAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.SpecialAsteroidBlock;
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.SpecialAsteroidBlockHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkProviderAsteroids.class, remap = false)
public class MixinChunkProviderAsteroids {

    @Final
    @Shadow
    private SpecialAsteroidBlockHandler coreHandler;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(World par1World, long par2, boolean par4, CallbackInfo ci){
        ChunkProviderAsteroids provider = (ChunkProviderAsteroids) (Object)this;

        String[] coreBlocksFromConfig = AsmodeusConfig.asteroidsCoreBlocks;
        for(String coreBlock : coreBlocksFromConfig)
        {

            String[] dataBlock = coreBlock.split(":");

            //Example: modid:block:meta:probability:thickness
            String blockModId = dataBlock[0];
            String blockName = dataBlock[1];
            int blockMeta = Integer.parseInt(dataBlock[2]);
            int blockProbabiblity = Integer.parseInt(dataBlock[3]);
            float blockThickness = Float.parseFloat(dataBlock[4]);

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockModId + ":" + blockName));

            coreHandler.addBlock(new SpecialAsteroidBlock(block, (byte)blockMeta, blockProbabiblity, blockThickness));

        }

    }
}
