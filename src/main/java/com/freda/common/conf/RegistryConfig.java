package com.freda.common.conf;

/**
 * 注册中心配置
 *
 * @author wukai
 */
public class RegistryConfig {

    private String ip;

    private int port;

    private int timeout = 1000;

    private String protocal;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getProtocal() {
        return protocal;
    }

    public void setProtocal(String type) {
        this.protocal = type;
    }

    public String getConnAddress() {
        return ip + ":" + port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocal == null) ? 0 : protocal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegistryConfig other = (RegistryConfig) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port != other.port)
            return false;
        if (protocal == null) {
            if (other.protocal != null)
                return false;
        } else if (!protocal.equals(other.protocal))
            return false;
        return true;
    }
    
    public String key() {
    	return ip + ":" + port;
    }
    
}
