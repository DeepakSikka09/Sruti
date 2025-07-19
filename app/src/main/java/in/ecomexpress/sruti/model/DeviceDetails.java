package in.ecomexpress.sruti.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceDetails {

    @JsonProperty("sdk_version_code")
    private int sdkVersionCode;

    @JsonProperty("app_version")
    private String app_version;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("is_otg_enabled")
    private boolean isOtgEnabled;

    @JsonProperty("device_time")
    public long getDevice_time() {
        return device_time;
    }

    @JsonProperty("device_time")
    public void setDevice_time(long device_time) {
        this.device_time = device_time;
    }

    @JsonProperty("device_time")
    private long device_time;


    @JsonProperty("sdk_version")
    private String sdkVersion;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("model_number")
    private String modelNumber;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonIgnore
    private String kernalVersion;

    @JsonIgnore
    private String osVersion;

    @JsonIgnore
    private String manufacturerOSVersion;

    @JsonIgnore
    private String deviceName;

    public void setSdkVersionCode(int sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

    public int getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setIsOtgEnabled(boolean isOtgEnabled) {
        this.isOtgEnabled = isOtgEnabled;
    }

    public boolean isIsOtgEnabled() {
        return isOtgEnabled;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceDetails{");
        sb.append("sdkVersionCode=").append(sdkVersionCode);
        sb.append(", app_version='").append(app_version).append('\'');
        sb.append(", deviceId='").append(deviceId).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", isOtgEnabled=").append(isOtgEnabled);
        sb.append(", device_time=").append(device_time);
        sb.append(", sdkVersion='").append(sdkVersion).append('\'');
        sb.append(", ipAddress='").append(ipAddress).append('\'');
        sb.append(", modelNumber='").append(modelNumber).append('\'');
        sb.append(", longitude=").append(longitude);
        sb.append(", manufacturer='").append(manufacturer).append('\'');
        sb.append(", kernalVersion='").append(kernalVersion).append('\'');
        sb.append(", osVersion='").append(osVersion).append('\'');
        sb.append(", manufacturerOSVersion='").append(manufacturerOSVersion).append('\'');
        sb.append(", deviceName='").append(deviceName).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceDetails)) return false;
        DeviceDetails that = (DeviceDetails) o;
        if (getSdkVersionCode() != that.getSdkVersionCode()) return false;
        if (getDeviceId() != that.getDeviceId()) return false;
        if (Double.compare(that.getLatitude(), getLatitude()) != 0) return false;
        if (isOtgEnabled != that.isOtgEnabled) return false;
        if (Double.compare(that.getLongitude(), getLongitude()) != 0) return false;
        if (!getSdkVersion().equals(that.getSdkVersion())) return false;
        if (!getIpAddress().equals(that.getIpAddress())) return false;
        if (!getModelNumber().equals(that.getModelNumber())) return false;
        return getManufacturer().equals(that.getManufacturer());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getSdkVersionCode();
        // result = 31 * result + (int) (getDeviceId() ^ (getDeviceId() >>> 32));
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isOtgEnabled ? 1 : 0);
        result = 31 * result + getSdkVersion().hashCode();
        result = 31 * result + getIpAddress().hashCode();
        result = 31 * result + getModelNumber().hashCode();
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getManufacturer().hashCode();
        return result;
    }

    public String getKernalVersion() {
        return kernalVersion;
    }

    public String getOSVersion() {
        return osVersion;
    }

    public String getManufacturerOSVersion() {
        return manufacturerOSVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }
}