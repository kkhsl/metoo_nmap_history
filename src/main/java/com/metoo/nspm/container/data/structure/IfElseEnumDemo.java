package com.metoo.nspm.container.data.structure;


import com.metoo.nspm.core.utils.MyStringUtils;

public enum IfElseEnumDemo {

    ROSE{
        @Override
        public String getFlowers(String ip, Integer mask) {
            int index = MyStringUtils.acquireCharacterPosition(ip, "\\\\.", 3);
            String parentIp = ip.substring(0, index);
            String segment = ".0";
            StringBuilder sb = new StringBuilder();
            sb.append(parentIp).append(segment);
            return sb.toString();
        }
    },
    TULIP{
        @Override
        public String getFlowers(String ip, Integer mask) {
            return "";
        }
    };

    public abstract String getFlowers(String ip, Integer mask);
    


}
