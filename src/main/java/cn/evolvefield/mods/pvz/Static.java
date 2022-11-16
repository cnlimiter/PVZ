package cn.evolvefield.mods.pvz;

import cn.evolvefield.mods.pvz.init.proxy.ClientProxy;
import cn.evolvefield.mods.pvz.init.proxy.CommonProxy;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.DistExecutor;
import org.slf4j.Logger;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:03
 * Description:
 */
public class Static {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "pvz";

    public static CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

}
