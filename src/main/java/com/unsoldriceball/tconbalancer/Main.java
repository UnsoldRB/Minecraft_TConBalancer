package com.unsoldriceball.tconbalancer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod(modid = Main.MOD_ID, acceptableRemoteVersions = "*")
public class Main {

    public static final String MOD_ID = "tconbalancer";
    private static final float DAMAGE_FACTOR = 0.75f;
    private static final float SPEED_FACTOR = 0.5f;
    private static final String[] TARGETS_ID =  new String[]
            {
                    "slimeknights.tconstruct.tools.common.entity.EntityArrow",
                    "slimeknights.tconstruct.tools.common.entity.EntityShuriken"
            };
    private static final String[] BOWS_ID = new String[]
            {
                    "slimeknights.tconstruct.tools.ranged.item.ShortBow",
                    "slimeknights.tconstruct.tools.ranged.item.LongBow"
            };




    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { //ModがInitializeを呼び出す前に発生するイベント。
        MinecraftForge.EVENT_BUS.register(this); //これでこのクラス内でForgeのイベントが動作するようになるらしい。
    }



    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event)
    {
        if (event.getEntity().world.isRemote) return;
        // ダメージを受けたエンティティが存在するか
        if (event.getEntityLiving() != null)
        {
            // ダメージを与えたエンティティを取得
            Entity attacker = event.getSource().getImmediateSource();
            if (attacker != null)
            {
                String attacker_id = attacker.getClass().toString();
                if (!isContainID(attacker_id, TARGETS_ID)) return;      //対象でないEntityならreturn

                // ダメージを(DAMAGE_FACTOR)%に減少させる
                float reducedDamage = event.getAmount() * DAMAGE_FACTOR;
                event.setAmount(reducedDamage);
            }
        }
    }



    //BOWS_IDに含まれる弓でバニラの矢を使用した際に、矢を減速させる関数。
    @SubscribeEvent
    public void onArrowSpawn(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote) return;

        Entity e = event.getEntity();
        if (e instanceof EntityTippedArrow || e instanceof EntitySpectralArrow)
        {
            EntityArrow arrow = (EntityArrow) e;
            Entity shooter = arrow.shootingEntity;
            if (shooter instanceof EntityPlayer)
            {
                EntityPlayer pl = (EntityPlayer) shooter;
                String bow = pl.getHeldItemMainhand().getItem().getClass().toString();

                if (isContainID(bow, BOWS_ID))
                {
                    arrow.motionX *= SPEED_FACTOR;
                    arrow.motionY *= SPEED_FACTOR;
                    arrow.motionZ *= SPEED_FACTOR;
                }
            }
        }
    }



    //引数の文字列にtargetsのいずれかが含まれていたらtrueを返す関数。
    private boolean isContainID(String id, String[] targets)
    {
        for (String tar: targets)
        {
            if (id.contains(tar))
            {
                return true;
            }
        }
        return false;
    }
}
