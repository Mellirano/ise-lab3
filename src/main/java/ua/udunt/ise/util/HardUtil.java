package ua.udunt.ise.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

@Slf4j
public class HardUtil {

    public static void logHardwareInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        GlobalMemory memory = hal.getMemory();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        int mb = 1024 * 1024;
        int cpus = cpu.getPhysicalProcessorCount();
        int cores = cpu.getLogicalProcessorCount();
        long mem = memory.getTotal() / mb;
        long xmx = memoryBean.getHeapMemoryUsage().getMax() / mb;
        long xms = memoryBean.getHeapMemoryUsage().getInit() / mb;
        String info = "{\"System CPU count\": " + cpus
                + ",\"System CPU cores\": " + cores
                + ",\"System memory size\": " + mem
                + ",\"Java initial memory size (Xms) (Mb)\": " + xms
                + ",\"Java max memory size (Xmx) (Mb)\": " + xmx;

        log.info("Hardware info: {}", info);
    }
}
