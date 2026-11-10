# Groovy 模板脚本项目 i18n

Status: resolved
Triage: ready-for-agent

## Problem Statement

Groovy 模板当前入口 UI 使用固定中文文案，项目名称也只有单一默认值，用户的 Development Kit 语言变化不会让新建 Groovy 项目显示对应语言。模板没有展示如何使用宿主已提供的脚本本地化 API，复制模板的开发者也缺少可直接运行的本地化范例。

## Solution

更新 Groovy 模板，让新建项目通过宿主 locale 展示英文或中文的项目名称、项目描述和入口 UI 文案。模板提供 `en.json` 与 `zh.json` 资源样例，脚本通过 `stringResources.getString(key)` 读取静态文案。UI 另外显示已本地化的语言标识，`Groovy` 作为稳定的语言专名保留原样。README 说明资源目录和 API 的使用方式。

## User Stories

1. As a Groovy script author, I want the template to include English and Chinese string catalogs, so that I can start a localized project without creating the resource structure myself.
2. As a Groovy script author, I want the sample project name to follow the host locale, so that the project list and installation preview match the language used by Development Kit.
3. As a Groovy script author, I want the sample project description to follow the host locale, so that project metadata is readable before the script starts.
4. As a Development Kit user, I want the counter label in the Groovy sample UI to follow the host locale, so that the running project is readable in English or Chinese.
5. As a Development Kit user, I want the counter button text to follow the host locale, so that the sample UI is consistently translated.
6. As a Development Kit user, I want the sample UI to identify itself as a Groovy project, so that I can distinguish it from the other language templates while checking the UI.
7. As a Groovy script author, I want the language label to use the same localization API as other sample text, so that the template demonstrates the complete i18n path.
8. As a script author, I want the English catalog to provide the base text and the Chinese catalog to provide translations, so that absent per-key translations fall back predictably.
9. As a script author, I want catalog filenames to use BCP 47 language tags, so that the resource layout follows the host script-localization contract.
10. As a Groovy script author, I want to read complete static strings using one `getString(key)` call, so that the example matches the small host API and needs no locale setter or formatting API.
11. As a Groovy template user, I want the README to explain how to add catalogs and call the localization API, so that I can extend the sample after copying it.
12. As a template maintainer, I want event payloads, test markers, and developer diagnostics to remain stable, so that localization does not change the sample's event and lifecycle behavior.

## Implementation Decisions

- This change belongs only to the Groovy template repository. The host already owns locale resolution and the `stringResources.getString(key)` contract.
- Script catalogs use the project's `res/strings` resource directory and normalized BCP 47 filenames. Ship `en` and `zh` examples.
- The catalogs provide `m8test.project.description` in English and Chinese. The project DSL has no description property, so the catalogs supply the localized description without changing the Gradle plugin. The configured/default English project name, including an `appName` override, remains the fallback; the Chinese catalog uses `m8test.project.name` to override it. The English catalog intentionally omits that name key so it cannot hide a developer's `appName`.
- The entry Compose UI obtains the counter label, button label, and a complete localized language-identification string from the catalog. Each catalog contains a complete static string; no formatting API or language setter is introduced.
- The language proper name `Groovy`, package names, command syntax, paths, event payloads, and developer diagnostics remain stable.
- The existing resource-copy and archive pipeline preserves `res/strings` without language-specific plugin changes.
- Update the README in its current documentation language with the BCP 47 catalog convention and Groovy API example; do not maintain localized README variants.
- Side scripts have no user-facing UI in this template. Their event and logging examples remain unchanged.

## Testing Decisions

- Validate externally visible behavior through the template's `rerunGroovy` task rather than testing private helper structure.
- Run the project on Development Kit at `192.168.31.207:5555` once with host locale English and once with host locale Chinese; verify the counter label, button label, and language marker under the button on the app UI. Confirm the catalogs include localized description and Chinese project name metadata while preserving the configured English `appName` fallback.
- If the Groovy Language Extension is not installed, install it from its language repository using the applicable `reinstall<Variant>Extension` task before rerunning the template.
- Keep a screenshot or equivalent device UI evidence for the localized run; verify the app screen rather than relying only on a successful build.
- The host resolver's catalog matching and fallback behavior is already covered by host tests; this repository verifies that the Groovy template packages and calls the API correctly.

## Out of Scope

- Changes to the host runtime, script API, Gradle plugin, or another language template repository.
- Translation of event payloads, test identifiers, package names, command syntax, file paths, and developer-facing diagnostics.
- Plural rules, interpolation, format strings, user-controlled locale selection, or additional catalogs beyond the English and Chinese examples.
- Localization of the README itself.

## Further Notes

- TypeScript and the other language templates have their own repository-local specifications and tickets.
- Kotlin will receive the same template i18n adaptation but is excluded from device verification because its current template may not work.
