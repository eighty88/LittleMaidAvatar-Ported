package maid.lib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Library.MODID)
@Mod.EventBusSubscriber(modid=Library.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Library {
    public static final String MODID = "maidlibrary";
    public static Dist dist = Dist.DEDICATED_SERVER;

    /** logger */
    public static Logger logger = LogManager.getLogger(Library.MODID);

    /**
     * コンストラクタ
     */
    public Library() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> Library.dist = Dist.CLIENT);


    }
}
