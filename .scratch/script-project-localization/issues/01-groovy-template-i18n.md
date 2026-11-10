# 01: Groovy 模板展示双语脚本项目

**What to build:** Groovy 模板生成的脚本项目能根据 Development Kit 的宿主语言显示本地化项目 metadata 和计数器 UI，并能在界面中识别其 Groovy 语言实现。

**Blocked by:** None (can start immediately).

**Status:** resolved

- [x] English and Chinese BCP 47 catalogs provide translated description, counter label, button label, and complete language-identification text; the Chinese catalog overrides the project name while the English name continues to respect `appName`.
- [x] The Groovy entry UI reads all user-visible starter text through the host `stringResources.getString(key)` API, with the localized language marker directly below the button; package names, event payloads, test markers, and diagnostics remain stable.
- [x] The generated project retains its catalogs, and project name and description resolve from the same metadata keys used by the host.
- [x] The README explains the resource convention and shows how Groovy scripts read a localized string.
- [x] `rerunGroovy` on Development Kit in English and Chinese host locales displays the expected translations on device `192.168.31.207`.
- [x] Device verification records visible UI evidence; install the Groovy Language Extension first with its repository task only if it is missing.

## Answer

The Groovy template now includes English and Chinese catalogs, uses `stringResources` for all starter UI text, and displays the localized Groovy language marker below the counter button. `rerunGroovy` passed on `192.168.31.207:5555` under both English and Chinese app locales; screenshots confirmed the UI, the counter button remained functional, and the app locale was restored to follow the device locale.
