package launcher.astanite.com.astanite.ui.settings;

public class UserMessages {
    private String name;
    private String email;
    private String message;
    private String deviceName;
    private String manufacturer;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getVersion_release() {
        return version_release;
    }

    public int getVersion() {
        return version;
    }

    private String version_release;
    private int version;

    public UserMessages(String name, String email, String message, String deviceName, String manufacturer, String version_release, int version) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.deviceName = deviceName;
        this.manufacturer = manufacturer;
        this.version_release = version_release;
        this.version = version;
    }

    UserMessages() {
    }

}
