package maid.model.api.renderer;

import com.mojang.blaze3d.vertex.Tesselator;
import maid.model.TextureOffset;
import maid.model.api.model.ModelBase;
import maid.model.api.model.caps.ModelBox;
import maid.model.api.model.caps.ModelBoxBase;
import maid.model.api.model.caps.ModelPlate;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Constructor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelRenderer {

    // ModelRenderer互換変数群
    public float textureWidth;
    public float textureHeight;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    protected boolean compiled;
    protected int displayList;
    public boolean mirror;
    public boolean showModel;
    public boolean isHidden;
    /**
     * パーツの親子関係に左右されずに描画するかを決める。
     * アーマーの表示などに使う。
     */
    public boolean isRendering;
    public List<ModelBoxBase> cubeList;
    public List<ModelRenderer> childModels;
    public final String boxName;
    protected ModelBase baseModel;
    public ModelRenderer pearent;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float scaleX;
    public float scaleY;
    public float scaleZ;


    //	public static final float radFactor = 57.295779513082320876798154814105F;
    public static final float radFactor = 180F / (float)Math.PI;
    //	public static final float degFactor = 0.01745329251994329576923690768489F;
    public static final float degFactor = (float)Math.PI / 180F;

    // SmartMovingに合わせるために名称の変更があるかもしれません。
    public int rotatePriority;
    public static final int RotXYZ = 0;
    public static final int RotXZY = 1;
    public static final int RotYXZ = 2;
    public static final int RotYZX = 3;
    public static final int RotZXY = 4;
    public static final int RotZYX = 5;

//	public static final int ModeEquip = 0x000;
//	public static final int ModeInventory = 0x001;
//	public static final int ModeItemStack = 0x002;
//	public static final int ModeParts = 0x010;
    protected ItemStack itemstack;

    public boolean adjust;
    public FloatBuffer matrix;
    public boolean isInvertX;

    //private Render renderBlocksIr = new RenderBlocks();



    public ModelRenderer(ModelBase pModelBase, String pName) {
        textureWidth = 64.0F;
        textureHeight = 32.0F;
        compiled = false;
        displayList = 0;
        mirror = false;
        showModel = true;
        isHidden = false;
        isRendering = true;
        cubeList = new ArrayList<>();
        baseModel = pModelBase;
        pModelBase.boxList.add(this);
        boxName = pName;
        setTextureSize(pModelBase.textureWidth, pModelBase.textureHeight);

        rotatePriority = RotXYZ;
        itemstack = ItemStack.EMPTY;
        adjust = true;
        matrix = BufferUtils.createFloatBuffer(16);
        isInvertX = false;

        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;

        pearent = null;

//		renderBlocksIr.useInventoryTint = false;
    }

    public ModelRenderer(ModelBase pModelBase, int px, int py) {
        this(pModelBase, null);
        setTextureOffset(px, py);
    }

    public ModelRenderer(ModelBase pModelBase) {
        this(pModelBase, (String)null);
    }

    public ModelRenderer(ModelBase pModelBase, int px, int py, float pScaleX, float pScaleY, float pScaleZ) {
        this(pModelBase, px, py);
        this.scaleX = pScaleX;
        this.scaleY = pScaleY;
        this.scaleZ = pScaleZ;
    }

    public ModelRenderer(ModelBase pModelBase, float pScaleX, float pScaleY, float pScaleZ) {
        this(pModelBase);
        this.scaleX = pScaleX;
        this.scaleY = pScaleY;
        this.scaleZ = pScaleZ;
    }

    // ModelRenderer互換関数群

    public void addChild(ModelRenderer pModelRenderer) {
        if (childModels == null) {
            childModels = new ArrayList<ModelRenderer>();
        }
        childModels.add(pModelRenderer);
        pModelRenderer.pearent = this;
    }

    public ModelRenderer setTextureOffset(int pOffsetX, int pOffsetY) {
        textureOffsetX = pOffsetX;
        textureOffsetY = pOffsetY;
        return this;
    }

    public ModelRenderer addBox(String pName, float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth) {
        addParts(ModelBox.class, pName, pX, pY, pZ, pWidth, pHeight, pDepth, 0.0F);
        return this;
    }

    public ModelRenderer addBox(float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth) {
        addParts(ModelBox.class, pX, pY, pZ, pWidth, pHeight, pDepth, 0.0F);
        return this;
    }

    public ModelRenderer addBox(float pX, float pY, float pZ,
                                int pWidth, int pHeight, int pDepth, float pSizeAdjust) {
        addParts(ModelBox.class, pX, pY, pZ, pWidth, pHeight, pDepth, pSizeAdjust);
        return this;
    }

    public ModelRenderer setRotationPoint(float pX, float pY, float pZ) {
        rotationPointX = pX;
        rotationPointY = pY;
        rotationPointZ = pZ;
        return this;
    }

    public void render(float par1, boolean pIsRender) {
        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(offsetX, offsetY, offsetZ);

        if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            setRotation();
        }
        renderObject(par1, pIsRender);
        GL11.glPopMatrix();
    }
    public void render(float par1) {
        render(par1, true);
    }

    public void renderWithRotation(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);

        setRotation();

        GL11.glCallList(displayList);
        GL11.glPopMatrix();
    }

    public void postRender(float par1) {
        if (isHidden) return;
        if (!showModel) return;

        if (!compiled) {
            compileDisplayList(par1);
        }

        if (pearent != null) {
            pearent.postRender(par1);
        }

        GL11.glTranslatef(offsetX, offsetY, offsetZ);

        if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F) {
            GL11.glTranslatef(rotationPointX * par1, rotationPointY * par1, rotationPointZ * par1);
        }
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F) {
            setRotation();
        }
    }

    protected void compileDisplayList(float par1) {
        displayList = GL11.glGenLists(1);
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        Tesselator tesselator = Tesselator.getInstance();

        for (int i = 0; i < cubeList.size(); i++) {
            cubeList.get(i).render(tesselator, par1);
        }

        GL11.glEndList();
        compiled = true;
    }

    public ModelRenderer setTextureSize(int pWidth, int pHeight) {
        textureWidth = pWidth;
        textureHeight = pHeight;
        return this;
    }


    // 独自追加分

    /**
     * ModelBox継承の独自オブジェクト追加用
     */
    public ModelRenderer addCubeList(ModelBoxBase pModelBoxBase) {
        cubeList.add(pModelBoxBase);
        return this;
    }

    protected ModelBoxBase getModelBoxBase(Class<? extends ModelBoxBase> pModelBoxBase, Object ... pArg) {
        try {
            Constructor<? extends ModelBoxBase> lconstructor =
                    pModelBoxBase.getConstructor(ModelRenderer.class, Object[].class);
            return lconstructor.newInstance(this, pArg);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object[] getArg(Object ... pArg) {
        Object lobject[] = new Object[pArg.length + 2];
        lobject[0] = textureOffsetX;
        lobject[1] = textureOffsetY;
        for (int li = 0; li < pArg.length; li++) {
            lobject[2 + li] = pArg[li];
        }
        return lobject;
    }

    public ModelRenderer addParts(Class<? extends ModelBoxBase> pModelBoxBase, String pName, Object ... pArg) {
        pName = boxName + "." + pName;
        TextureOffset ltextureoffset = baseModel.getTextureOffset(pName);
        setTextureOffset(ltextureoffset.textureOffsetX(), ltextureoffset.textureOffsetY());
        addCubeList(getModelBoxBase(pModelBoxBase, getArg(pArg)).setBoxName(pName));
        return this;
    }

    public ModelRenderer addParts(Class<? extends ModelBoxBase> pModelBoxBase, Object ... pArg) {
        addCubeList(getModelBoxBase(pModelBoxBase, getArg(pArg)));
        return this;
    }

    /**
     * 自分でテクスチャの座標を指定する時に使います。
     * コンストラクタへそのまま値を渡します。
     */
    public ModelRenderer addPartsTexture(Class<? extends ModelBoxBase> pModelBoxBase, String pName, Object ... pArg) {
        pName = boxName + "." + pName;
        addCubeList(getModelBoxBase(pModelBoxBase, pArg).setBoxName(pName));
        return this;
    }

    /**
     * 自分でテクスチャの座標を指定する時に使います。
     * コンストラクタへそのまま値を渡します。
     */
    public ModelRenderer addPartsTexture(Class<? extends ModelBoxBase> pModelBoxBase, Object ... pArg) {
        addCubeList(getModelBoxBase(pModelBoxBase, pArg));
        return this;
    }


    public ModelRenderer addPlate(float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane) {
        addParts(ModelPlate.class, pX, pY, pZ, pWidth, pHeight, pFacePlane, 0.0F);
        return this;
    }

    public ModelRenderer addPlate(float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane, float pSizeAdjust) {
        addParts(ModelPlate.class, pX, pY, pZ, pWidth, pHeight, pFacePlane, pSizeAdjust);
        return this;
    }

    public ModelRenderer addPlate(String pName, float pX, float pY, float pZ,
                                  int pWidth, int pHeight, int pFacePlane) {
        addParts(ModelPlate.class, pName, pX, pY, pZ, pWidth, pHeight, pFacePlane, 0.0F);
        return this;
    }

    /**
     * 描画用のボックス、子供をクリアする
     */
    public void clearCubeList() {
        cubeList.clear();
        compiled = false;
        if (childModels != null) {
            childModels.clear();
        }
    }

    /**
     *  回転変換を行う順序を指定。
     * @param pValue
     * Rot???を指定する
     */
    public void setRotatePriority(int pValue) {
        rotatePriority = pValue;
    }

    /**
     * 内部実行用、座標変換部
     */
    protected void setRotation() {
        // 変換順位の設定
        switch (rotatePriority) {
            case RotXYZ:
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                break;
            case RotXZY:
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                break;
            case RotYXZ:
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                break;
            case RotYZX:
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                break;
            case RotZXY:
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                break;
            case RotZYX:
                if (rotateAngleX != 0.0F) {
                    GL11.glRotatef(rotateAngleX * radFactor, 1.0F, 0.0F, 0.0F);
                }
                if (rotateAngleY != 0.0F) {
                    GL11.glRotatef(rotateAngleY * radFactor, 0.0F, 1.0F, 0.0F);
                }
                if (rotateAngleZ != 0.0F) {
                    GL11.glRotatef(rotateAngleZ * radFactor, 0.0F, 0.0F, 1.0F);
                }
                break;
        }
    }

    /**
     * 内部実行用、レンダリング部分。
     */
    protected void renderObject(float par1, boolean pRendering) {
        // レンダリング、あと子供も
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, matrix);
        if (pRendering && isRendering) {
            GL11.glPushMatrix();
            GL11.glScalef(scaleX, scaleY, scaleZ);
            GL11.glCallList(displayList);
            GL11.glPopMatrix();
        }

        if (childModels != null) {
            for (int li = 0; li < childModels.size(); li++) {
                childModels.get(li).render(par1, pRendering);
            }
        }
    }

    /**
     * パーツ描画時点のマトリクスを設定する。 これ以前に設定されたマトリクスは破棄される。
     */
    public ModelRenderer loadMatrix() {
        GL11.glLoadMatrixf(matrix);
        if (isInvertX) {
            GL11.glScalef(-1F, 1F, 1F);
        }
        return this;
    }


    // ゲッター、セッター

    public boolean getMirror() {
        return mirror;
    }

    public ModelRenderer setMirror(boolean flag) {
        mirror = flag;
        return this;
    }

    public boolean getVisible() {
        return showModel;
    }

    public void setVisible(boolean flag) {
        showModel = flag;
    }


    // Deg付きは角度指定が度数法

    public float getRotateAngleX() {
        return rotateAngleX;
    }

    public float getRotateAngleDegX() {
        return rotateAngleX * radFactor;
    }

    public float setRotateAngleX(float value) {
        return rotateAngleX = value;
    }

    public float setRotateAngleDegX(float value) {
        return rotateAngleX = value * degFactor;
    }

    public float addRotateAngleX(float value) {
        return rotateAngleX += value;
    }

    public float addRotateAngleDegX(float value) {
        return rotateAngleX += value * degFactor;
    }

    public float getRotateAngleY() {
        return rotateAngleY;
    }

    public float getRotateAngleDegY() {
        return rotateAngleY * radFactor;
    }

    public float setRotateAngleY(float value) {
        return rotateAngleY = value;
    }

    public float setRotateAngleDegY(float value) {
        return rotateAngleY = value * degFactor;
    }

    public float addRotateAngleY(float value) {
        return rotateAngleY += value;
    }

    public float addRotateAngleDegY(float value) {
        return rotateAngleY += value * degFactor;
    }

    public float getRotateAngleZ() {
        return rotateAngleZ;
    }

    public float getRotateAngleDegZ() {
        return rotateAngleZ * radFactor;
    }

    public float setRotateAngleZ(float value) {
        return rotateAngleZ = value;
    }

    public float setRotateAngleDegZ(float value) {
        return rotateAngleZ = value * degFactor;
    }

    public float addRotateAngleZ(float value) {
        return rotateAngleZ += value;
    }

    public float addRotateAngleDegZ(float value) {
        return rotateAngleZ += value * degFactor;
    }

    public ModelRenderer setRotateAngle(float x, float y, float z) {
        rotateAngleX = x;
        rotateAngleY = y;
        rotateAngleZ = z;
        return this;
    }

    public ModelRenderer setRotateAngleDeg(float x, float y, float z) {
        rotateAngleX = x * degFactor;
        rotateAngleY = y * degFactor;
        rotateAngleZ = z * degFactor;
        return this;
    }

    public float getRotationPointX() {
        return rotationPointX;
    }

    public float setRotationPointX(float value) {
        return rotationPointX = value;
    }

    public float addRotationPointX(float value) {
        return rotationPointX += value;
    }

    public float getRotationPointY() {
        return rotationPointY;
    }

    public float setRotationPointY(float value) {
        return rotationPointY = value;
    }

    public float addRotationPointY(float value) {
        return rotationPointY += value;
    }

    public float getRotationPointZ() {
        return rotationPointZ;
    }

    public float setRotationPointZ(float value) {
        return rotationPointZ = value;
    }

    public float addRotationPointZ(float value) {
        return rotationPointZ += value;
    }

    public ModelRenderer setScale(float pX, float pY, float pZ) {
        scaleX = pX;
        scaleY = pY;
        scaleZ = pZ;
        return this;
    }

    public float setScaleX(float pValue) {
        return scaleX = pValue;
    }

    public float setScaleY(float pValue) {
        return scaleY = pValue;
    }

    public float setScaleZ(float pValue) {
        return scaleZ = pValue;
    }

}
