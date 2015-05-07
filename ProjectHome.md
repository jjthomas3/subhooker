# Introduction #

> subHooker is a script-free pre-commit and post-commit hook package that substantially eases the configuration of subversion post-commit e-mail messages. It requires no external Perl scripts, no external mail clients. subHooker uses a properties file, simply configure it, and call it from your existing hook. If you want to get more advanced, it supports a complete HTML or plain text based e-mail template system.

# Features #

  * Supports standard logging modes (debug, info, warn, error)
  * Pre-commit hook features
    * Enforce required commit message, configurable
    * Enforce minimal commit message length, configurable
    * Enforce required work, defect, or backlog item, configurable using standard Java RegEX
  * Post-commit hook features
    * Configurable email template system, for both plain and html e-mail.
    * Post-commit hook email is configurable:
      * Turn Diff on or off
      * Turn Change List (Changed Paths) on off.
      * Plain Text or HTML Based e-mail.
    * Email client built in, simply configure the SMTP preferences in properties file.
  * Supports Localization using standard I18N message bundles.

**subHooker Requires Java 1.6** Runtime Environment to run.

## Tested on: ##
  * Fedora Core 14
  * Fedora Core 15
  * Windows 7
  * Windows Server 2008
  * Windows Server 2008 `R2`
  * Windows Vista
  * Windows XP