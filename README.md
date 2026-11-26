# Boost in AAPS 4.0

Boost is an independently maintained variant of [AAPS](https://github.com/nightscout/AndroidAPS)
4.0.0 that adds the Boost APS plugin. It is not a release of, or supported by, the AAPS project.
You build it yourself and you are responsible for how you use it. AGPL-3.0, as AAPS.

This line is Boost 6.3.3: the V6 meal logic and the Boost high temp, with each SMB sized by the
Boost tiers as in Boost 6.2.0 for AAPS 3.4.2.6.

6.3.3 changes the default of Boost max IOB to 12 U, which makes Boost use your max IOB (up to 12 U), and stops
auto-config copying max IOB into it. A value you set lower is kept. It also lets the committed cap go
up to 5.0 U, keeps the sleep detector from starting sleep before your night start when no heart rate
reaches the phone, adds a light version of the Boost overview, and logs a night insulin-need check
that never notifies. It otherwise doses as 6.3.2.

6.3.2 sets the two SMB minutes settings to 15 (standard) and 20 (UAM) once, the first time Boost
runs, and then leaves them to you. 6.3.1 lowered a higher value on every loop cycle, so neither
could be raised. A value 6.3.1 lowered stays as it is until you change it. Settings imported from
AAPS without Boost have both reset, so they start at 15 and 20 in the same way; settings from a
Boost build keep theirs. 6.3.2 also keeps the night mode BG offset correction from 6.3.1, and
otherwise doses as 6.3.1.

## Dosing changes from Boost 6.2.0

- There is one Boost plugin, and it runs V6. If you ran plain Boost on 3.4, you move to V6 dosing
  when you install this, and your sensitivity settings are converted once on the first loop cycle.
- Percent scale is on at 200 % and no longer has a setting. Where 3.4 had stored it as off, it
  stays off.
- The two "Max minutes of basal to limit SMB" settings are set to 15 (standard) and 20 (UAM) the
  first time Boost runs, and you can change them after that. These settings limit only the SMB that
  oref sizes: Tier 8, and every SMB while Boost is off, such as at night. V6's meal doses and Boost's
  other tiers are not limited by them.
- In a meal state, V6 used to give its dose on a cycle where V1 had suspended for a low. It no
  longer does. V6's rapid-rise check now takes the largest of the latest change, the short average
  and the long average, as V1 does, where it used to take the latest change only.
- The lowering of the low-glucose threshold on an accelerating rise needs glucose to be rising, and
  never goes below your low-glucose setting.
- Hypo Caution above 1.0 lowers the smallest dose V6 keeps when the hypo risk is high.
- Sleep detection:
  - A sleep that begins in the 90 minutes before your night window is held, unless steps, or heart
    rate with steps, show you are up again.
  - In the 90 minutes before your night ends, walking ends sleep: steps over the last hour reaching
    your sleep-in steps setting, with at least 100 of them in the last 15 minutes.
  - Watch steps are assessed more conservatively, each step being counted once.
  - Health Connect step top-ups no longer count as a reason to wake.
- The insulin-need watch compares the last 5 days with your usual, at 1.5 standard deviations.
- During a meal, V6 used to move to its cautious state (RECOVERING, 0.4× of the dose) as soon as a
  rise slowed, while glucose was still climbing. It now stays in the meal state until the rise is
  below 3 mg/dL (0.17 mmol/L) per 5 minutes, and returns to it when glucose rises faster than
  8 mg/dL (0.44 mmol/L) per 5 minutes.
- The early primer starts on a sustained rise, a short average of at least 3 mg/dL (0.17 mmol/L) per
  5 minutes, where it used to need acceleration. It no longer fires on a second rise once a meal has
  been committed.
- RECOVERING is capped at the committed cap, so it never gives more in one cycle than the meal state
  does.
- Boost max IOB defaults to 12 U, its maximum. Every Boost dose is already held to your max IOB, so at
  12 Boost uses your max IOB and follows it when it changes, stopping at 12 U when max IOB is higher.
  Set a lower value to keep Boost doses below it. Auto-config no longer copies max IOB into it. When you install this, a value nobody chose
  is moved to 12 once: the old default of 1.0, or the copy auto-config made. Any other value is kept.
  For anyone who had 1.0 or an old copy, Boost can now give more insulin, up to their max IOB.
- The committed cap can be set up to 5.0 U, where it stopped at 2.5 U, and auto-config uses the same
  range. Auto-config sets it from your history as the larger of your typical SMB and a fortieth of
  your total daily dose, so it goes above 2.5 U only when your daily dose is above about 100 U or
  your typical SMB is above 2.5 U. The weekly re-derivation moves the cap you have, including one you
  set, with changes in that derivation, up to 5.0 U at most. The primer stays at 2.5 U at most. Its
  default (0.5 U) is unchanged.
- Without heart rate (no reading in the last 24 hours), the sleep detector starts sleep no earlier than
  your night start. In 6.3.2 it could start up to 90 minutes before it, through an evening meal, and
  while it reads sleeping the V6 meal logic and the Boost tiers do not dose. A bedtime learned from
  past nights can still move night start later, but not earlier. With heart rate it works as before.

## Other changes

- The APS screen has a Heart rate section and an Auto config section, which shows Boost's automatic
  settings in words.
- With "Open the Boost overview at start" on, the app opens on the Boost overview and stays there
  when a dialog such as Maintenance opens.
- The Boost overview and widget no longer show a loop result older than 15 minutes as current.
  Low-glucose suspend shows as LGS, and delta acceleration and profile % are shown again.
- The tab row on the Boost overview holds Manage and the pump, APS, loop, BG source and NSClient
  pages; every other screen is in the menu and search. "Show tabs on the Boost overview" hides it.
- The V6 pre-meal target is shown and entered in your profile's units.
- On the watch, the ranged glucose and DynISF complications go blank when the phone is out of
  contact or the reading is old.
- "Meal-detection Sensitivity" is renamed "Insulin budget scale". It works as before.
- The Boost overview has a light version, which follows the AAPS colour scheme setting.
- The insulin-need watch also checks insulin from midnight to 6 am over the last 3 nights against the
  14 before. It only writes the result to the log (ndn= in the reason) and never notifies.

## Auto-config

Auto-config sets Boost's V6 settings from your own history, so you do not have to tune them at the
start. The first setup needs at least 7 days with a total daily dose and 1,500 glucose readings in
the last 14 days, and uses those 14 days. It is tried at most once an hour until there is enough
history. Straight after it, and then every 7 days, a weekly check runs on the last 28 days. The
first weekly check only records where the dose caps, Aggression and Hypo Caution sit, so it does
not move them. The Auto config
section on the APS screen shows what was set and why.

It reads your time below 70 mg/dL (3.9 mmol/L) and below 54 mg/dL (3.0 mmol/L), your median total
daily dose, the size of your SMBs and your manual boluses.

At the first setup, a setting you have changed yourself is kept. A value counts as yours when it is
different from every default Boost has shipped. The weekly check does not replace Aggression, Hypo
Caution or the two dose caps. It moves them with the change in the value it derives: Aggression and
Hypo Caution by the same amount, and the confirmed and committed caps by the same proportion. A
value you set therefore keeps its distance (or, for a cap, its ratio) to the derived one. The
cumulative SMB cap and the primer are worked out again from the caps, unless you have set them
yourself. A switch or a release threshold you have set is never changed.

### Low-glucose bands

| Band | Your time below range |
|---|---|
| Well controlled | below 70 under 1.5 % and below 54 under 0.3 % |
| Default | neither of the other two |
| Hypo-prone | below 70 over 6 %, or below 54 over 1.5 % |

### What it sets

| Setting | How it is set | Range at first setup | At the weekly check |
|---|---|---|---|
| Aggression | 1.0; 0.92 when time below 70 is over 4 %; 0.85 when hypo-prone | 0.85 to 1.0 | Moves by the same amount as the derived value. A new value must come out twice in a row. Never raised above 1.0. |
| Hypo Caution | 1.0, plus 0.25 for each point that time below 70 is over 4 %, plus 0.5 for each point that time below 54 is over 1 %, rounded to 0.1 | 1.0 to 2.0 | As Aggression |
| Confirmed dose cap | The larger of your 90th percentile manual bolus (with at least 10 of them) and your 95th percentile SMB | 1.5 to 7.5 U | Moves in proportion to the derived value, at most 25 % in one check. A change of 0.47 U or less is not written. |
| Committed dose cap | The larger of your 75th percentile SMB and your daily dose divided by 40 | 0.25 to 5.0 U | As the confirmed cap. A change of 0.07 U or less is not written. |
| Cumulative SMB cap (60 min) | Confirmed cap plus twice the committed cap | 1.0 to 10.0 U | Worked out again from the two caps |
| Acceleration primer | Committed cap times 0.75 (well controlled), 0.6 (default) or 0.375 (hypo-prone) | Up to the committed cap, and at most 2.5 U | Worked out again from the committed cap. A change of 0.056 U or less is not written. |
| Primer delivery (bolus or temp basal) | Bolus when well controlled, temp basal otherwise | | Temp basal after one check that is not well controlled. Bolus again only after two well-controlled checks in a row. "Force bolus primer" overrides it, and auto-config never changes that setting. |
| Fast-carb fast-path | On, except when hypo-prone | | Off on the first check that does not allow it. On again only after two checks in a row that allow it. |
| Aggressive early confirm | On only when well controlled | | As the fast-carb fast-path |
| Velocity-budget floor (high tail) | On only when well controlled | | Not checked again |
| Confirm tranche: release threshold | Measured from your own confirm episodes in the last 45 days. Needs at least 15, with 5 small and 5 large. | 0.20 to 0.80 | Raised when the measured value is higher. Never lowered in this release, because lowering it releases more insulin. |
| Boost Bolus Cap | Copied from your AAPS maximum bolus | 0.1 to 10.0 U | Not checked again |

A check with less than 7 days or 1,500 glucose readings changes nothing and is skipped. It does
not reset a run of two checks.
After the first setup, the weekly check can move a setting outside the range of the first setup, as
far as the setting's own limits. Auto-config also sets the two "Max minutes of basal to limit SMB"
settings once, and moved Boost max IOB to 12 U once (see Dosing changes above).

### Limits on adding insulin

Your time below range decides whether auto-config may add insulin, not only how much.

- When time below 70 is over 4 %, or time below 54 is 1 % or more, auto-config holds changes that
  add insulin.
  - At the first setup the confirmed, committed and cumulative caps are not raised. The new value
    is shown as a suggestion only and is not written later. The primer is still set up, and it is
    given as a temp basal.
  - At the weekly check a rise in any cap, the primer or Aggression is held, and so is a fall in
    Hypo Caution. The held change is kept and is written at a later check when time below range is
    back under these limits.
  - The fast-carb fast-path and aggressive early confirm are not switched on while this holds.
  - Changes that give less insulin always go through.
- Aggressive early confirm and the velocity-budget floor also have a live check. They work only
  while your time below 63 mg/dL (3.5 mmol/L) over the last 14 days is under 2 % and your time
  below 70 is under 3.5 %. The check is done every hour. With fewer than 1,000 glucose readings in
  the 14 days, both stay off.
- Auto-config never raises Aggression above 1.0.

Your time below range does not change how the caps are sized. That comes from your SMB sizes and
your daily dose. It decides whether a higher value may be written, and the size of the primer.

## Installing over your current build

Android installs over an existing AAPS only when the new APK is signed with the same key. The
workflow below uses the signing key already held in your repository's secrets. Android 12 or later
is required.

Before you start, export your settings in AAPS and keep your current APK. The move to 4.0 changes
the database and the settings store, and neither can be read by an older build. To go back, you
reinstall the older APK after uninstalling and import the export.

The same applies to a standard AAPS 4.0 build. It installs over Boost, because the package and
version code are the same, but it cannot open Boost's database and the app stops at start, so the
loop does not run. To move from Boost to standard AAPS, export your settings, uninstall Boost,
install AAPS and import the export.

### You already build AAPS in the browser

1. In your existing AndroidAPS fork on GitHub, choose Add file, Create new file, and name it
   `.github/workflows/boost-ci.yml`.
2. Paste in the contents of [boost-ci.yml](.github/workflows/boost-ci.yml) and commit.
3. Under Actions, run Boost CI and select the release.
4. Install `boost-<release>.apk` from the `AAPS/<release>` folder in your Google Drive, over your
   current app.

Your fork's `KEYSTORE_SET` and `GDRIVE_OAUTH2` secrets are used as they are.

### You have a fork of Boost-in-AAPS_3.4

This repository was Boost-in-AAPS_3.4. Its Boost 6.2.0 for AAPS 3.4.2.6 is now on the branch
`dev-3.4.2.6`, and the earlier 6.1.2 line that was `master` is on `master-3.4.2.6`. `master` and
`dev` are both this 4.0 line. A fork of it already holds the secrets Boost CI needs, including the
separate keystore secrets.

- To stay on 3.4, do not sync your fork's `master` or `dev`. Branch CI builds them as before.
- To move to 4.0, sync your fork. GitHub says your `master` has diverged and offers to discard your
  commits; accepting replaces it with this `master`, which brings Boost CI. Then run Boost CI and
  install as above. After the sync, Branch CI is gone from your fork and 3.4 can no longer be built
  from it, so keep your current APK and a settings export.

### You are new to browser builds

Fork this repository, enable Actions, and create the two secrets with the
[AAPS browser build instructions](https://androidaps.readthedocs.io/en/latest/SettingUpAaps/BrowserBuild.html).
Then run Boost CI. To reuse the key from another repository, run CI Keystore Export there first.

### You build in Android Studio

Check out the release tag and build `fullRelease` with your existing keystore.

## After installing

Open Config Builder and check that OpenAPS Boost is the selected APS. Check your maximum IOB and
SMB settings, then watch the first cycles in the APS tab before relying on it.

[Advanced settings](_docs/advanced-settings.md) lists every setting under Advanced Settings and how each one changes dosing.

## Tools

The [Simulator](https://diabettech.github.io/Boost/boost_simulator.html) shows how Boost sizes a dose
from a glucose level, trend and IOB; choose Boost 6.3.2 at the top. The
[Analyser](https://diabettech.github.io/Boost/boost_analyser.html) compares V1 and V6 dosing on your
own Nightscout data. The [Tuning Guide](https://diabettech.github.io/Boost/boost_tuning_guide.html)
explains each setting for 6.3.2 and 6.2.0.
