package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.client.render.RenderLandVehicle;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.entity.vehicle.EntityLawnMower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class RenderLawnMower extends RenderLandVehicle<EntityLawnMower>
{
    public RenderLawnMower(RenderManager renderManager)
    {
        super(renderManager);
        this.setEnginePosition(0, 10.5, -9, 180, 1.2);
        this.setFuelPortPosition(-4.75, 12.5, 3.5, -90);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 6.0F, 3.0F, 13.5F, 1.15F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 6.0F, 3.0F, 13.5F, 1.15F);
        this.addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 5.0F, 3.6F, -10.7F, 1.55F);
        this.addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 5.0F, 3.6F, -10.7F, 1.55F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityLawnMower entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityLawnMower entity, double x, double y, double z, float currentYaw, float partialTicks)
    {
        RenderHelper.enableStandardItemLighting();

        float additionalYaw = entity.prevAdditionalYaw + (entity.additionalYaw - entity.prevAdditionalYaw) * partialTicks;

        EntityLivingBase entityLivingBase = (EntityLivingBase) entity.getControllingPassenger();
        if(entityLivingBase != null)
        {
            entityLivingBase.renderYawOffset = currentYaw - additionalYaw;
            entityLivingBase.prevRenderYawOffset = currentYaw - additionalYaw;
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-currentYaw, 0, 1, 0);
            GlStateManager.rotate(additionalYaw, 0, 1, 0);

            //TODO clean this up
            if(entity.canTowTrailer())
            {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(180F, 0, 1, 0);

                Vec3d towBarOffset = entity.getTowBarVec();
                GlStateManager.translate(towBarOffset.x, towBarOffset.y + 0.5, -towBarOffset.z);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.towBar, ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
            }

            GlStateManager.translate(0, 0, 0.65);
            GlStateManager.scale(1.25, 1.25, 1.25);

            this.setupBreakAnimation(entity, partialTicks);

            double bodyOffset = 0.5625;

            //Render the body
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(entity.body, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            //Render the handles bars
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(0, bodyOffset + 0.4, -0.15);
                GlStateManager.rotate(-45F, 1, 0, 0);
                GlStateManager.scale(0.9, 0.9, 0.9);

                float wheelAngle = entity.prevWheelAngle + (entity.wheelAngle - entity.prevWheelAngle) * partialTicks;
                float wheelAngleNormal = wheelAngle / 45F;
                float turnRotation = wheelAngleNormal * 25F;
                GlStateManager.rotate(turnRotation, 0, 1, 0);

                Minecraft.getMinecraft().getRenderItem().renderItem(entity.steeringWheel, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();

            super.doRender(entity, x, y, z, currentYaw, partialTicks);
        }
        GlStateManager.popMatrix();
        EntityRaytracer.renderRaytraceElements(entity, x, y, z, currentYaw);
    }
}
