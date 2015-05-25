package cn.iam007.pic.clean.master.utils;

public class StringUtils {

    public static class Unit {
        Unit(String count, String unit) {
            this.count = count;
            this.unit = unit;
        }

        public String toString() {
            return count + unit;
        };

        /**
         * @return the count
         */
        public String getCount() {
            return count;
        }

        /**
         * @return the unit
         */
        public String getUnit() {
            return unit;
        }

        String count;
        String unit;
    }

    /**
     * 将字节数转换为单位类
     * 
     * @param size
     * @return
     */
    public static Unit convertFileSize(long size) {
        /**
         * 几百Bytes
         * 几百KB
         * 几点几几MB
         * 十几点几MB
         * 几十MB
         * 几百MB
         * 几点几GB
         * 十几GB
         */
        String count = "";
        String unit = "";
        if (size < 1024) {
            // 小于1K
            count = "" + size;
            unit = "Bytes";
        } else if (size < 1024 * 1024) {
            // 小于1M
            count = "" + (size / 1024);
            unit = "KB";
        } else if (size < 1024 * 1024 * 10) {
            // 小于10M
            count = String.format("%.2f", size / (1024.0 * 1024.0));
            unit = "MB";
        } else if (size < 1024 * 1024 * 1024) {
            // 小于1GB
            count = "" + (size / 1024 / 1024);
            unit = "MB";
        } else {
            // 大于1GB
            count = String.format("%.1f", size / (1024.0 * 1024.0 * 1024));
            unit = "GB";
        }

        return new Unit(count, unit);
    }

    //    /**
    //     * 将字节数转换为可读字符串
    //     * 
    //     * @param size
    //     * @return
    //     */
    //    public static String convertFileSize2String(long size) {
    //        /**
    //         * 几百Bytes
    //         * 几百KB
    //         * 几点几几MB
    //         * 十几点几MB
    //         * 几十MB
    //         * 几百MB
    //         * 几点几GB
    //         * 十几GB
    //         */
    //        String count = "";
    //        String unit = "";
    //        if (size < 1024) {
    //            // 小于1K
    //            count = "" + size;
    //            unit = "Bytes";
    //        } else if (size < 1024 * 1024) {
    //            // 小于1M
    //            count = "" + (size / 1024);
    //            unit = "KB";
    //        } else if (size < 1024 * 1024 * 10) {
    //            // 小于10M
    //            count = String.format("%.2f", size / (1024.0 * 1024.0));
    //            unit = "MB";
    //        } else if (size < 1024 * 1024 * 1024) {
    //            // 小于1GB
    //            count = "" + (size / 1024 / 1024);
    //            unit = "MB";
    //        } else {
    //            // 大于1GB
    //            count = String.format("%.1f", size / (1024.0 * 1024.0 * 1024));
    //            unit = "GB";
    //        }
    //
    //        return count + unit;
    //    }
}
