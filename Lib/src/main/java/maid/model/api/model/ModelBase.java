package maid.model.api.model;

import maid.model.TextureOffset;
import maid.model.api.caps.IModelCaps;
import maid.model.api.renderer.ModelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;

import java.util.*;

public abstract class ModelBase extends AbstractModelBase {

    public static final float PI = (float)Math.PI;

    public EntityRenderer<?> render;

    // ModelBaseとある程度互換
    public int textureWidth = 64;
    public int textureHeight = 32;
    public float onGrounds[] = new float[] {0.0F, 0.0F};
    public int dominantArm = 0;
    public boolean isRiding = false;
    public boolean isChild = true;
    public List<ModelRenderer> boxList = new ArrayList<ModelRenderer>();
    private final Map<String, TextureOffset> modelTextureMap = new HashMap<>();

    // ModelBase互換関数群

    public void render(IModelCaps entityCaps, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean isRender) {
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, IModelCaps entityCaps) {
    }

    public void setLivingAnimations(IModelCaps entityCaps, float limbSwing, float limbSwingAmount, float partialTickTime) {
    }

    public ModelRenderer getRandomModelBox(Random par1Random) {
        // 膝に矢を受けてしまってな・・・
        int li = par1Random.nextInt(this.boxList.size());
        ModelRenderer lmr = this.boxList.get(li);
        for (int lj = 0; lj < boxList.size(); lj++) {
            if (!lmr.cubeList.isEmpty()) {
                break;
            }
            // 箱がない
            if (++li >= boxList.size()) {
                li = 0;
            }
            lmr = this.boxList.get(li);
        }
        return lmr;
    }

    protected void setTextureOffset(String par1Str, int par2, int par3) {
        modelTextureMap.put(par1Str, new TextureOffset(par2, par3));
    }

    /**
     * 推奨されません。
     */
    public TextureOffset getTextureOffset(String par1Str) {
        // このままだと意味ないな。
        return modelTextureMap.get(par1Str);
    }


    // Mthトンネル関数群

    public static float mh_sin(float f) {
        f = f % 6.283185307179586F;
        f = (f < 0F) ? 360 + f : f;
        return Mth.sin(f);
    }

    public static float mh_cos(float f) {
        f = f % 6.283185307179586F;
        f = (f < 0F) ? 360 + f : f;
        return Mth.cos(f);
    }

    public static float mh_sqrt(float f) {
        return Mth.sqrt(f);
    }

    public static float mh_sqrt(double d) {
        return Mth.sqrt((float) d);
    }

    public static int mh_floor(float f) {
        return Mth.floor(f);
    }

    public static int mh_floor(double d) {
        return Mth.floor(d);
    }

    public static long mh_floor_long(double d) {
        return Mth.floor(d);
    }

    public static float mh_abs(float f) {
        return Mth.abs(f);
    }

    public static double mh_abs_max(double d, double d1) {
        return Mth.absMax(d, d1);
    }

    public static int mh_bucketInt(int i, int j) {
        return Mth.intFloorDiv(i, j);
    }

    public static boolean mh_stringNullOrLengthZero(String s) {
        return s == null || s.equals("");
    }

    public static int mh_getRandomIntegerInRange(Random random, int i, int j) {
        return Mth.getInt(String.valueOf(random.nextInt()), i, j);
    }

}