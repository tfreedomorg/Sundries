package me.totalfreedom.sundries.config;

public enum ConfigEntry
{
    MESSAGES_SUCCESS_COLOR("messages.success_color"),
    MESSAGES_HIGHLIGHT_COLOR("messages.highlight_color"),
    TELEPORT_INVULNERABILITY_SECONDS("teleport.invulnerability_seconds"),
    TELEPORT_BACK_ON_DEATH("teleport.back_on_death"),
    TELEPORT_REQUEST_TIMEOUT_SECONDS("teleport.request_timeout_seconds"),
    TELEPORT_MAX_PENDING_REQUESTS("teleport.max_pending_requests");

    private final String path;

    ConfigEntry(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
}