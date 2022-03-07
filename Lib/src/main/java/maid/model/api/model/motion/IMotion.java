package maid.model.api.model.motion;

import maid.model.api.caps.IModelCaps;
import maid.model.api.model.ModelLittleMaidBase;

/**
 * メイドさんのモーション割込み用インターフェース
 *
 * 1.18.2へのポート時に名前変更
 *
 * @author firis-games
 *
 */
public interface IMotion {

    public String getMotionId();

    public boolean postRotationAngles(ModelLittleMaidBase model, String motion, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, IModelCaps entityCaps);

}
