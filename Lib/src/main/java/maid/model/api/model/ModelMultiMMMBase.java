package maid.model.api.model;

import maid.model.api.caps.IModelCaps;

import static maid.model.api.caps.IModelCaps.caps_textureLightColor;

/**
 * MMMの実験コードを含む部分。
 * ModelMultiBaseに追加するに足りるかをここで実験。
 * このクラスにある機能は予告なく削除される恐れが有るためご留意下さい。
 *
 * 後方互換用に保持・基本的に使わない想定
 */
public abstract class ModelMultiMMMBase extends ModelMultiBase {

    /**
     * 削除予定変数使わないで下さい。
     * Beverly7で使用しているので削除は見送り対応を考える
     */
    @Deprecated
    public float onGround;
    /**
     * 削除予定変数使わないで下さい。
     * Beverly7で使用しているので削除は見送り対応を考える
     */
    @Deprecated
    protected float heldItemLeft = 0.0F;
    /**
     * 削除予定変数使わないで下さい。
     * Beverly7で使用しているので削除は見送り対応を考える
     */
    @Deprecated
    protected float heldItemRight = 0.0F;


    public ModelMultiMMMBase() {
        super();
    }
    public ModelMultiMMMBase(float sizeAdjust) {
        super(sizeAdjust);
    }
    public ModelMultiMMMBase(float sizeAdjust, float yOffset, int textureWidth, int textureHeight) {
        super(sizeAdjust, yOffset, textureWidth, textureHeight);
    }

    @Override
    public Object getCapsValue(int pIndex, Object... pArg) {
        switch (pIndex) {
            case caps_textureLightColor:
                return getTextureLightColor((IModelCaps)pArg[0]);
        }
        return super.getCapsValue(pIndex, pArg);
    }

    public float[] getTextureLightColor(IModelCaps pEntityCaps) {
        return null;
    }

}
