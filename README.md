# PublicRandomPermissionGiver

PublicRandomPermissionGiver is a lightweight Minecraft plugin that allows server administrators to randomly assign permissions to players from predefined permission groups.

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![API Version](https://img.shields.io/badge/api--version-1.21-yellow.svg)
![Folia Support](https://img.shields.io/badge/folia--support-yes-brightgreen.svg)

## Features

- Create groups of permissions with configurable settings
- Randomly assign permissions to players from these groups
- Custom messages when permissions are granted
- Fallback option for when a player already has all permissions in a group
- Full Folia compatibility
- Integration with Quagmire Core
- PlaceholderAPI support

## Requirements

- Java 21 or higher
- Paper/Spigot 1.21+
- Quagmire Core
- PlaceholderAPI (optional)

## Installation

1. Download the latest version of PublicRandomPermissionGiver
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin by creating permission groups in the `groups` directory

## Usage

### Configuration

The plugin uses a configuration system where you define permission groups in YAML files within the `groups` directory. Each group can contain multiple permissions with display names and associated commands.

Example group configuration:

```yaml
id: example
message: "You have been granted the %name% permission!"
permissions:
  permission1:
    id: permission1
    display-name: "Example Permission 1"
    permission: "example.permission.1"
    commands:
      - "give %player% diamond 1"
  permission2:
    id: permission2
    display-name: "Example Permission 2"
    permission: "example.permission.2"
    commands:
      - "give %player% gold_ingot 5"
fallback:
  message: "You already have all permissions in this group!"
  commands:
    - "give %player% emerald 1"
```

### Commands

- `/randompermissions` or `/randomperms` or `/rp` - Base command for the plugin
- `/rp issue <player> <group>` - Issues a random permission from the specified group to the player
- `/rp reload` - Reloads the plugin configuration

### Permissions

- `randompermissions.command` - Permission to use the plugin's base command
- `randompermissions.reload` - Permission to reload the plugin
- `randompermissions.issue` - Permission to issue random permissions to players

## Developer API

For developers looking to integrate with PublicRandomPermissionGiver, you can access the plugin instance and permission manager:

```java
RandomPermissionsPlugin plugin = (RandomPermissionsPlugin) Bukkit.getPluginManager().getPlugin("RandomPermissions");
PermissionGroupManager permissionManager = plugin.getPermissionGroupManager();

// Example: Check if a group exists
boolean exists = permissionManager.groupExists("example");

// Example: Get a group
PermissionGroup group = permissionManager.getGroup("example");

// Example: Issue a random permission to a player
group.issue(player).thenAccept(entry -> {
    // Permission granted successfully
}).exceptionally(ex -> {
    // Error handling
    return null;
});
```

## Support

If you encounter any issues or have questions, feel free to create an issue on our GitHub repository.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

We welcome contributions to the project! If you'd like to contribute:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -am 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Credits

- **Author**: Quagmire (me@quagmire.dev) 