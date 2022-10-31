package cn.evolvefield.mods.pvz.api.interfaces.paz;

import cn.evolvefield.mods.pvz.api.interfaces.base.IBodyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/31 1:03
 * Description:
 */
public interface IZombieModel <T extends LivingEntity & IZombieEntity> {

    /**
     * use for drop part entity to render.
     * not for current entity.
     * {link @ZombieBodyRender}
     */
    void tickPartAnim(IBodyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    /**
     * render drop body part.
     * {link @ZombieBodyRender}
     */
    void renderBody(IBodyEntity entity, PoseStack stack, VertexBuffer buffer, int packedLight, int packedOverlay);

    //@OnlyIn(Dist.CLIENT)
    EntityModel<T> getZombieModel();
}
