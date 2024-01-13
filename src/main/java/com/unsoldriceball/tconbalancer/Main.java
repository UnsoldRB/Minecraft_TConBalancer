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
public class Main
{
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




    //ModがInitializeを呼び出す前に発生するイベント。
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //これでこのクラス内でForgeのイベントが動作するようになるらしい。
        MinecraftForge.EVENT_BUS.register(this);
    }




    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event)
    {
        //イベントがサーバー側で発生、かつダメージを受けたエンティティが存在するか
        if (event.getEntity().world.isRemote) return;
        if (event.getEntityLiving() != null)
        {
            // ダメージを与えたエンティティを取得
            final Entity L_ATTACKER = event.getSource().getImmediateSource();
            if (L_ATTACKER != null)
            {
                final String L_ID_ATTACKER = L_ATTACKER.getClass().toString();
                //対象となるEntityか調べる。
                if (isContainID(L_ID_ATTACKER, TARGETS_ID))
                {
                    // ダメージを(DAMAGE_FACTOR)%に減少させる
                    float reducedDamage = event.getAmount() * DAMAGE_FACTOR;
                    event.setAmount(reducedDamage);
                }
            }
        }
    }



    //BOWS_IDに含まれる弓でバニラの矢を使用した際に、矢を減速させる関数。
    @SubscribeEvent
    public void onArrowSpawn(EntityJoinWorldEvent event)
    {
        if (!event.getWorld().isRemote)
        {

            final Entity L_ENTITY = event.getEntity();

            if (L_ENTITY instanceof EntityTippedArrow || L_ENTITY instanceof EntitySpectralArrow)
            {

                final EntityArrow L_ARROW = (EntityArrow) L_ENTITY;
                Entity L_SHOOTER = L_ARROW.shootingEntity;

                if (L_SHOOTER instanceof EntityPlayer)
                {

                    final EntityPlayer L_PLAYER = (EntityPlayer) L_SHOOTER;
                    final String L_BOW = L_PLAYER.getHeldItemMainhand().getItem().getClass().toString();

                    if (isContainID(L_BOW, BOWS_ID))
                    {
                        L_ARROW.motionX *= SPEED_FACTOR;
                        L_ARROW.motionY *= SPEED_FACTOR;
                        L_ARROW.motionZ *= SPEED_FACTOR;
                    }
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
