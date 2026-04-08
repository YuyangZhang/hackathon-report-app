# Global Development Rules

## Windows Environment Policy

### Local Debugging Commands
- All local debugging commands must be optimized for Windows environment
- Use PowerShell syntax for command execution
- Windows path separators (backslashes `\`) should be used in file paths
- Windows-specific commands and tools should be prioritized

### Command Guidelines

1. **Shell Preference**: Use PowerShell as the default shell for all commands
2. **Path Format**: Use Windows path format (e.g., `C:\Users\username\project`)
3. **Environment Variables**: Use Windows environment variable syntax (`%VAR%` or `$env:VAR`)
4. **File Operations**: Use Windows-compatible file commands (e.g., `dir` instead of `ls`)
5. **Process Management**: Use Windows process management commands
6. **Networking**: Use Windows networking commands and tools

### Implementation Examples

- **Directory Listing**: `dir` or `Get-ChildItem`
- **File Copy**: `Copy-Item` or `copy`
- **Process Kill**: `Stop-Process` or `taskkill`
- **Environment Variables**: `$env:PATH` or `%PATH%`
- **Service Management**: `Get-Service`, `Start-Service`, `Stop-Service`

### Development Tools

- Prefer Windows-native development tools
- Use Windows-compatible package managers (Chocolatey, Scoop)
- Configure IDE settings for Windows development
- Use Windows-specific debugging tools and profilers

### Gradle Wrapper Location

- **Gradle Wrapper Path**: `D:\Work\github\hackathon-report-app\backend\gradle\wrapper`
- **Wrapper Files**: All Gradle wrapper executables and configuration files are located in the specified directory
- **Build Commands**: Use `gradlew.bat` from the backend directory for Windows builds
- **Wrapper Properties**: Gradle wrapper properties file is located at `gradle\wrapper\gradle-wrapper.properties`

### Backend Package Management

- **Package Manager**: Gradle for backend project management
- **Gradle Installation**: `D:\Work\Software\gradle-8.6\bin`
- **Gradle Executable**: Use `gradle.bat` or add to PATH for direct Gradle commands
- **Build System**: Spring Boot project with Gradle build configuration

## Purpose

This policy ensures all local development and debugging commands work seamlessly in Windows environments, providing consistent and reliable development experience for Windows users.
