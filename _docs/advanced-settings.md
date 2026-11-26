# Boost advanced settings

This page lists every setting under Advanced Settings in the Boost settings screen of Boost 6.3.3.
To get there, open Config Builder, tap the cog next to Boost, and choose Advanced Settings. The
screens are listed here in the order the app shows them.

The settings on the first Boost screen (Aggression, Hypo Caution, Boost Max IOB, Max basal, night
mode on or off, night mode BG offset, Enable SMB and Enable UAM) are not covered here.

Some settings are set and moved by auto-config. They are marked "Auto-config" in the table. A
value you set yourself is kept at the first setup, and the weekly check moves some of them. See
[Auto-config](../README.md#auto-config) for the rules.

## Words used on this page

- Boost active: the Boost tiers and the V6 meal doses run only while Boost is active. Boost is
  inactive in the night window, while the sleep detector reads you as asleep, during a morning
  lie-in, and while a temp target above the DynISF normal target is running (unless "Allow Boost
  with High Temp Targets" is on). While Boost is inactive, only the plain oref SMB is given.
- V6 meal states: V6 watches a rise (observing), then confirms a meal and gives one larger dose
  (confirmed), then gives smaller doses each cycle while the meal continues (committed). When the
  rise slows it moves to a cautious state (recovering). Outside a confirmed or committed meal, V6
  never gives more than the base engine would, apart from the acceleration primer and the
  velocity-budget floor.
- Boost tiers: the base engine sizes each SMB with one of eight tiers. Tier 8 is plain oref.
- One cycle is one loop run, normally every 5 minutes.

## Advanced Settings

The first 15 settings come first. Then come the links to the next two screens, and then "Enable
Circadian ISF" and "Allow Boost with High Temp Targets".

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Insulin budget scale | 1.0 | 0.8–1.2 | Multiplies the insulin budget V6 works from in every meal state. | Higher gives more V6 meal insulin, up to 20 % more at 1.2. Lower gives up to 20 % less. The dose caps below still apply. |
| Confirmed dose cap (U) | 2.5 U | 0–7.5 U | Upper limit on the single dose V6 gives when it confirms a meal. Auto-config. | Higher allows a larger confirmed dose. Lower makes it smaller. |
| Committed dose cap (U) | 0.5 U | 0–5.0 U | Upper limit on each V6 dose per cycle in the committed and recovering states. Also limits the two floors below. Auto-config. | Higher allows larger follow-on meal doses each cycle. |
| Fast-carb fast-path | On | On/off | Confirms a meal in one cycle when the rise is sharp and speeding up and the meal score agrees. Auto-config. | On gives the confirmed dose earlier on fast carbs. Off waits for the normal confirm. It never runs after glucose below 80 mg/dL (4.4 mmol/L) in the last 60 minutes. It also waits while you are asleep or exercising. |
| Aggressive early confirm | Off | On/off | Confirms a meal one cycle sooner when the meal signal is strong on two cycles in a row. Auto-config. | On gives the confirmed dose one cycle earlier, which adds a little insulin overall. It works only while your 14-day time below 63 mg/dL (3.5 mmol/L) is under 2 % and below 70 mg/dL (3.9 mmol/L) is under 3.5 %, and stays off with fewer than 1,000 readings in the 14 days. |
| Phase-3 composed brake floor | Off | On/off | In a meal, stops V6's stacked brakes from cutting the dose to nothing. | On gives at least 25 % of the V6 budget per cycle, up to the committed cap, when glucose is above 160 mg/dL (8.9 mmol/L) and predicted glucose is more than 20 mg/dL (1.1 mmol/L) above target. Same low-glucose check as above. |
| Velocity-budget floor (high tail) | Off | On/off | Gives a small dose when glucose is high but oref predicts it will come down without more insulin. Auto-config. | On gives up to 0.5 U per cycle (and no more than the committed cap and your IOB room) above 180 mg/dL (10.0 mmol/L). Not while asleep or after a recent low. Not in the recovering state either. Same low-glucose check as above. |
| Acceleration primer (U) | 0 U | 0–2.5 U | Largest early dose V6 gives on a rise before a meal is confirmed. 0 turns it off. Auto-config. | Higher gives more insulin earlier in an unannounced meal. See the note below the table. |
| Force bolus primer | Off | On/off | Gives the primer as an SMB even when auto-config chose the temp basal route. Auto-config never changes this switch. | On gives the primer at once, and it cannot be taken back. As a temp basal it is spread over 30 minutes and is skipped when the loop is already lowering basal. |
| Confirm tranche | Off | On/off | Splits the confirmed dose: part now, the rest held for 10 minutes. | On can only give less. The held part is given only if the rise continues and V6 is still in the committed state. Otherwise it is dropped. |
| Confirm tranche: fraction given immediately | 0.5 | 0.3–1.0 | Share of the confirmed dose given at once when the tranche is on. | Lower holds back more. At 1.0 nothing is held, so the tranche has no effect. |
| Confirm tranche: release threshold | 0.48 | 0.20–0.80 | How likely a large rise must look before the held part is given. Auto-config. | Higher releases the held part less often, so less insulin. |
| Anticipatory pre-meal target | Off | On/off | Learns your usual meal times from V6 meal confirms and lowers the target before them. | On gives more insulin from the lead time until 45 minutes before a usual meal (15 minutes at the default). See the note below the table. |
| Pre-meal target | 72 mg/dL (4.0 mmol/L) | 65–90 mg/dL (3.6–5.0 mmol/L) | The target used in the pre-meal window. | Lower gives more insulin before the meal. It is used only when it is below your current target. |
| Pre-meal lead time (min) | 60 min | 30–90 min | How long before a learned meal the low target starts. It always ends 45 minutes before the meal. | Longer gives a longer window with the low target, so more insulin before the meal. Values below 55 minutes act as 55. |
| Enable Circadian ISF | Off | On/off | Multiplies ISF by a fixed time-of-day curve. | See the note below the table. |
| Allow Boost with High Temp Targets | Off | On/off | Keeps Boost active while a temp target above the DynISF normal target is running. | On keeps the Boost tiers and V6 meal doses running under a high temp target, so more insulin. Off switches Boost off for those cycles. |

The acceleration primer starts in the observing state when glucose rises by at least 3 mg/dL
(0.17 mmol/L) in 5 minutes, both in the latest change and in the short average. It reaches the full
amount at a rise of 8 mg/dL (0.44 mmol/L) in 5 minutes. The amount is reduced below 110 mg/dL
(6.1 mmol/L) and above 180 mg/dL (10.0 mmol/L), with none at or below 90 mg/dL (5.0 mmol/L) or at or
above 220 mg/dL (12.2 mmol/L), as IOB nears max IOB, and when the hypo-risk model
is high. It is given once per meal. It is not given after an announced meal (carbs entered or a
manual bolus), after glucose below 80 mg/dL (4.4 mmol/L) in the last 60 minutes, while asleep or
while exercising. Primer insulin above one primer amount is taken off the confirmed dose that
follows.

The pre-meal target needs a meal time seen at least 6 times on at least 4 different days in the
last 60 days. It is not used in the night window, while asleep, while any temp target is running,
after a recent low, or during exercise or post-exercise recovery.

With "Enable Circadian ISF" on, ISF is multiplied by a value that depends on the hour. A value
below 1 makes ISF stronger and gives more insulin. A value above 1 gives less.

| Hour | ISF multiplied by |
|---|---|
| 02:00–12:59 | 0.49–0.84 (lowest, 0.49, at 08:00) |
| 13:00 | 1.06 |
| 14:00 | 1.34 |
| 15:00–22:59 | 1.00 |
| 23:00 | 1.82 |
| 00:00–01:59 | 1.33, then 1.16 |

"Allow Boost with High Temp Targets" also affects the post-exercise recovery target that Boost sets
itself. With the default (off), the recovery target switches Boost off. Then only the plain oref
SMB is left, and only if "Enable SMB with high temp target" is on (also off by default). So with
default settings the recovery window gives no SMB at all.

## Boost base-engine SMB sizing

These two settings size the base SMB (the Boost tiers). They also limit V6 outside a confirmed or
committed meal, because there V6 never gives more than the base engine (apart from the primer and the
velocity-budget floor).

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Boost Insulin Required % | 50 % | 30–100 % | Share of oref's insulin need given as one SMB in Tiers 1, 2, 7 and 8. The other tiers start from it. | Higher gives larger SMBs. At 50 % an insulin need of 2 U gives a 1 U SMB before the caps; at 100 % it gives 2 U. |
| Boost Scale Value | 1.0 | 0.1–3.0 | Multiplies the dose that Tiers 3, 4 and 6 start from, which is your basal rate per hour. It is also multiplied by the profile %. | Higher gives larger Tier 3, 4 and 6 doses, up to the Boost Bolus Cap. When scale times profile % reaches 3 or more, Tiers 3 and 4 stop. |

Boost Bolus Cap, the largest single Boost tier dose, is not on this screen. Auto-config copies it
from your AAPS maximum bolus. Percent scale is not on this screen either. It is on at 200 %.

## Default AAPS Settings

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Max IOB for SMB | 3.0 U | 0–70 U | The AAPS max IOB. No SMB or high temp is given above it. Every Boost dose is held to the lower of this and Boost Max IOB. | Higher allows more insulin on board. With Boost Max IOB at its default of 12 U, this is the limit that applies. In Simple Mode it is worked out from the patient type. |
| Use Autosens | On | On/off | Lets AAPS Autosens measure your sensitivity from recent glucose. | Used only when "Enable TDD-based ISF" is off and "Use traditional Autosens when TDD is off" is on. Then it scales basal, ISF, target and the remaining carb absorption time, by 0.7 to 1.2 with the AAPS defaults. The carb ratio is not scaled. |
| High temp target raises sensitivity | Off | On/off | Weakens ISF while a temp target above the DynISF normal target is running. | On gives less insulin. A 140 mg/dL (7.8 mmol/L) target with the normal target at 99 mg/dL (5.5 mmol/L) weakens ISF by the Autosens minimum, 0.7, which is about 30 % less insulin. |
| Low temp target lowers sensitivity | Off | On/off | Strengthens ISF while a temp target below the DynISF normal target is running. | On gives more insulin. An 80 mg/dL (4.4 mmol/L) target strengthens ISF by the Autosens maximum, 1.2. |

## Dynamic ISF (TDD-based)

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Enable TDD-based ISF | Off | On/off | Works out ISF from your total daily dose (TDD) and current glucose. Off uses your profile ISF. | On can give more or less insulin than your profile ISF, depending on your TDD. If the TDD looks far too low for your profile, profile ISF is used for that cycle. |
| Enable TDD sensitivity adjustment | Off | On/off | Divides ISF by the last 24 hours of insulin over the 7-day average. Needs TDD-based ISF. | A heavier last day gives a stronger ISF and higher basal, so more insulin. A lighter day gives less. Limited to the Autosens range. |
| Use traditional Autosens when TDD is off | On | On/off | With TDD-based ISF off, lets Autosens adjust basal, ISF, target and carb absorption time. | On follows Autosens: more insulin when it finds resistance, less when it finds sensitivity. Off applies no Autosens change, though a temp-target sensitivity setting can still apply. No effect while TDD-based ISF is on. |
| DynISF normal target | 99 mg/dL (5.5 mmol/L) | 70–120 mg/dL (3.9–6.7 mmol/L) | The glucose level the ISF formula is anchored to. A temp target above it counts as high and switches Boost off. | Higher lets higher temp targets run with Boost still active. With TDD-based ISF on and BG impact below 100 %, a higher value gives a stronger ISF. |
| BG impact on ISF (%) | 100 % | 0–100 % | How much current glucose changes ISF. Needs TDD-based ISF; set to 0 when it is switched off. | Higher gives a stronger ISF above the normal target (more insulin) and a weaker one below it (less insulin). 0 keeps ISF at its target value. |
| DynISF BG cap | 210 mg/dL (11.7 mmol/L) | 100–300 mg/dL (5.6–16.7 mmol/L) | Above this, only a third of the extra glucose counts when working out ISF. | Lower gives a weaker ISF at high glucose, so less insulin there. Only matters with TDD-based ISF on. |
| TDD adjustment factor (%) | 100 % | 1–300 % | Multiplies the TDD used in the ISF formula. | Higher gives a stronger ISF and more insulin. At 120 % the ISF number falls by about 17 %, which gives 20 % more insulin for a correction. Only matters with TDD-based ISF on. |

With the default settings, TDD-based ISF is off, so the sensitivity adjustment, BG impact, BG cap and
TDD adjustment factor have no effect. DynISF normal target still sets which temp targets count as high,
and the temp-target sensitivity settings still use it as their reference. Switching TDD-based ISF on sets BG impact to
100 %, and switching it off sets BG impact to 0 and turns the sensitivity adjustment off.

## Exercise Settings: Step Count Settings

Steps come from the phone and from a watch, whichever is higher. With no step data, none of these
apply.

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Inactivity Step Count | 500 | 0–1000 steps | Fewer steps than this in the last hour counts as inactive. | Higher counts more hours as inactive, so more insulin. Not in the night window, while asleep, in a lie-in, while Boost is inactive, or with a profile % other than 100. |
| Inactivity Profile Percent | 130 % | 100–200 % | Profile % used while inactive. | Higher gives more insulin while inactive. At 130 % basal and the Boost scale are multiplied by 1.3, and ISF is divided by 1.3. |
| Sleep-in Hours | 2 h | 0–18 h | How long after Night Mode End Time a low step count still counts as a lie-in. 0 turns the lie-in off. | During a lie-in Boost is off, and with night mode on the night mode SMB rules apply, so less insulin. Longer gives a longer lie-in window. |
| Sleep-in Steps | 250 | 0–1000 steps | Fewer steps than this in the last hour after the night end keeps the lie-in going. | Higher keeps Boost off for longer in the morning. |
| 5-Minute Activity Steps | 420 | 0–5000 steps | More steps than this in 5 minutes counts as active. 0 turns this check off. | Lower detects activity more easily. Active means less insulin (see Activity Profile Percent). |
| 15-Minute Activity Steps | 800 | 0–10000 steps | As above, over 15 minutes. | As above. |
| 30-Minute Activity Steps | 1200 | 0–10000 steps | As above, over 30 minutes. | As above. |
| 60-Minute Activity Steps | 1800 | 0–10000 steps | As above, over 60 minutes. | As above. |
| Activity Profile Percent | 80 % | 30–150 % | Profile % used while active. | Lower gives less insulin while active. 80 % gives 0.8 times the basal and an ISF 1.25 times weaker. The target also goes to 150 mg/dL (8.3 mmol/L) unless a temp target is running. |

Any one of the four step windows is enough to count as active. The activity profile % is used
only when your profile is at 100 %. Activity also damps V6's meal detection and stops the primer.

## Exercise Settings: Heart Rate Integration

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Enable HR Integration | Off | On/off | Uses heart rate from a watch to sort activity into types, and to check that low steps mean rest. | Mostly less insulin. Vigorous exercise lowers the profile by a further 10 points (not below 50 %). Strength exercise keeps the profile at 100 % and sets the target to 160 mg/dL (8.9 mmol/L). The inactivity raise needs a resting heart rate: with no usable heart rate, it is not applied. |
| Maximum Heart Rate (HRmax) | 180 bpm | 150–220 bpm | Top of your heart rate range for the heart rate zones. | Higher puts the same heart rate in a lower zone, so exercise is detected less often. |
| Resting Heart Rate | 60 bpm | 30–100 bpm | Bottom of your heart rate range. Replaced by a learned daytime value once enough nights are recorded. | Higher puts the same heart rate in a lower zone. |
| HR Averaging Window (minutes) | 15 min | 5–60 min | How many minutes of heart rate are averaged. | Shorter reacts faster to a change in heart rate. No direct effect on dose size. |
| Enable Stress Detection | Off | On/off | Treats a raised heart rate with very few steps as stress or illness. | On sets the target to 160 mg/dL (8.9 mmol/L) while it lasts, unless a temp target is running, so less insulin. It also damps V6's meal detection. Needs "Enable HR Integration" on. |

## Exercise Settings: Post-Exercise Recovery

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Enable Post-Exercise Recovery | Off | On/off | After exercise ends, sets a temp target and reduces SMBs for a recovery window. | On gives less insulin after exercise. The temp target is set only if no other temp target is running. V6's budget is halved in the window. |
| Recovery Window Duration (hours) | 2 h | 0.5–8 h | Length of the recovery window. | Longer gives less insulin for longer. The exercise type changes it (see the table below). |
| Recovery TempTarget | 144 mg/dL (8.0 mmol/L) | 90–200 mg/dL (5.0–11.1 mmol/L) | The temp target set for the window. 10 mg/dL (0.6 mmol/L) is added after strength exercise. | Higher gives less insulin. Any value above the DynISF normal target switches Boost off (see the note below). |
| SMB Reduction Factor | 0.5 | 0–1.0 | Multiplies the Boost Scale Value and the Boost Bolus Cap in the window. | Lower gives smaller Boost tier doses. The exercise type changes it (see the table below), and it is kept between 0.1 and 1.0. |
| Minimum Exercise Duration (min) | 10 min | 1–120 min | Exercise must last this long before a recovery window starts. | Higher means short activity does not start a window. |

The type of exercise that just ended changes the window and the factor. The type comes from heart rate when HR integration is on.

| Exercise type | Window length | Target | SMB Reduction Factor |
|---|---|---|---|
| Vigorous | × 1.25 | as set | × 0.8 |
| Strength (resistance) | × 1.5 | + 10 mg/dL (0.6 mmol/L) | × 1.2 |
| Light | × 0.5 | as set | × 1.4 |
| Other activity | as set | as set | as set |

With default settings the recovery target is above the DynISF normal target and above 100 mg/dL
(5.6 mmol/L). So Boost is off and SMBs are off for the whole window, and only the temp basal works
toward the target. The SMB Reduction Factor changes the dose only while Boost is active and SMBs
are allowed. That is when you cancel the target by hand (the window and the factor carry on), or
when both "Allow Boost with High Temp Targets" and "Enable SMB with high temp target" are on. If
glucose rebounds after a low (lowest glucose in the last hour below 100 mg/dL (5.6 mmol/L) and now
20 mg/dL (1.1 mmol/L) above it), Boost cancels its own recovery target and ends the window, and the
factor stops with it. V6's halving of its budget does not use this setting.

## Night Mode

The night window (from Night Mode Start Time to Night Mode End Time) switches Boost off whether
night mode is on or not. Night mode on, from the first Boost screen, also stops all SMBs in the
window while glucose is below your profile target plus the night mode BG offset.

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Night Mode Start Time | 22:00 | HH:mm | Start of the night window. | Earlier switches Boost off earlier in the evening, so less insulin then. |
| Night Mode End Time | 07:00 | HH:mm | End of the night window, in the phone's time zone. A lie-in can extend it (see Sleep-in Hours). | Later keeps Boost off for longer in the morning. |
| Disable Night Mode when COB > 0 | Off | On/off | Lifts the night mode SMB stop while carbs are on board. | On allows the plain oref SMB at night after carbs, so more insulin. Boost stays off in the window. |
| Disable Night Mode with low temp target | Off | On/off | Lifts the night mode SMB stop while a temp target below your profile target is running. | On allows the plain oref SMB at night under a low temp target. Boost stays off in the window. |
| Auto-detect sleep (HR + steps) | Off | On/off | Lets the sleep detector extend the night: earlier before bed and later while still asleep. | On keeps Boost off for longer, and with night mode on also keeps the night mode SMB stop on longer, so less insulin. Without heart rate in the last 24 hours, sleep does not start before your night start. |
| Pre-sleep lead time (minutes) | 60 min | 0–180 min | How long before night start the detector may enter its pre-sleep state. | Longer can start the night rules earlier. Only with Auto-detect sleep on. |
| Sleep confirmation window (minutes) | 10 min | 5–30 min | How long heart rate and steps must look like sleep before sleep is confirmed. | Longer confirms sleep later, so Boost stays on later in the evening. |
| Wake confirmation window (minutes) | 5 min | 2–15 min | How long heart rate above 1.25 times resting and at least 100 steps must last before you count as awake. | Longer keeps the sleep rules on for longer in the morning. |
| Use Health Connect for HR | Off | On/off | Reads heart rate from Android Health Connect. | No direct effect. It feeds HR integration and sleep detection. |
| Health Connect poll interval (minutes) | 5 min | 1–30 min | How often Health Connect is read. | No direct effect. |
| Activity-load shadow (steps) | On | On/off | Reads daily steps from Health Connect and logs what an activity-based ISF change would do. | None. It only writes to the log. |
| Grant Health Connect access | | | Asks for the Read Heart Rate permission. | None. |

V6 stops dosing while the sleep detector reads you as asleep, whether Auto-detect sleep is on or
not.

## Safety Settings

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Enable SMB with high temp target | Off | On/off | Allows the base SMB while a temp target above 100 mg/dL (5.6 mmol/L) is running. | Off gives no base SMB under such a target. Under a target above the DynISF normal target Boost is also off, unless allowed, so this is the only SMB left. |
| Enable SMB always | On | On/off | Allows the base SMB at all times. | Off allows the base SMB only under the switches below it, so less insulin at other times. |
| Enable SMB with COB | On | On/off | Allows the base SMB while carbs are on board. Shown only when "Enable SMB always" is off. | Off gives less insulin after entered carbs. |
| Enable SMB with low temp target | On | On/off | Allows the base SMB while a temp target below 100 mg/dL (5.6 mmol/L) is running. Shown only when "Enable SMB always" is off. | Off gives less insulin under a low temp target. |
| Enable SMB after carbs | On | On/off | Allows the base SMB for 6 hours after carbs are entered. Shown only when "Enable SMB always" is off. | Off gives less insulin after entered carbs. |
| SMB interval | 3 min | 1–10 min | Shortest time between two SMBs. The loop applies it to every SMB, V6 doses included. | Shorter allows SMBs more often. |
| Loop at sensor cadence | Off | On/off | Runs the loop on every sensor reading. | On a 1-minute sensor, on allows a decision every minute, subject to the SMB interval. No effect on a 5-minute sensor. |
| Max minutes of basal to limit SMB | 15 min (set once by Boost when nothing is stored; key default 30) | 15–120 min | Limits a Tier 8 SMB to this many minutes of basal, when IOB is at or below −0.2 U. | Higher allows larger Tier 8 SMBs. |
| UAM max minutes of basal to limit SMB | 20 min (set once by Boost when nothing is stored; key default 30) | 15–120 min | The same limit when IOB is above −0.2 U, which is most of the time. | Higher allows larger Tier 8 SMBs. At 20 minutes and a basal of 1.2 U/h the limit is 0.4 U. This is the limit on most SMBs while Boost is inactive, for example at night. Shown only with Enable UAM on, but applied either way. |
| Carbs required threshold | 1 g | 1–100 g | Smallest carb suggestion that raises a notification. | None. |
| Cumulative SMB cap (60 min) | 10 U | 0–10 U | Once SMBs in the last 60 minutes reach this, no SMB is given that cycle. One SMB can carry the total past it. 0 turns it off. Auto-config. | Lower gives less insulin in a long rise. It applies to V6 doses and Boost tiers alike. |

The two "Max minutes" settings limit only Tier 8, the plain oref SMB. Boost's other tiers and V6's
meal doses are not limited by them. When Boost is active, Tier 8 can go above this limit on a sharp
rise above 180 mg/dL (10.0 mmol/L), up to the Boost Bolus Cap.

The SMB switches in this table control the base SMB only. V6 checks only the main Enable SMB
switch and whether Boost is active. In a confirmed or committed meal it can still dose when the
switches here would stop the base SMB. In practice this matters only with "Allow Boost with High
Temp Targets" on, or with "Enable SMB always" off.

## Advanced Settings (inside the Boost advanced screen)

The first item is a link to the OpenAPS documentation on these limits.

| Setting | Default | Range | What it does | Effect on dosing |
|---|---|---|---|---|
| Always use short average delta | Off | On/off | Not read by Boost in this build. | None. |
| Post-rescue tight-ramp trial | Off | On/off | Joins a trial of the guard that runs after a low (lowest glucose in the last 45 minutes below 75 mg/dL (4.2 mmol/L)). | On, on randomly chosen days, caps doses in that window at 60 % and keeps the cap above 170 mg/dL (9.4 mmol/L). It never gives more than with it off. Leave off unless enrolled. |
| Max daily safety multiplier | 3.0 | 1.0–10.0 | No temp basal above this many times your highest profile basal rate. | Higher allows higher temp basals. |
| Current basal safety multiplier | 4.0 | 1.0–10.0 | No temp basal above this many times your current basal rate. | Higher allows higher temp basals. This also limits a primer given as a temp basal. |

Your max basal setting also limits every temp basal. The lowest of the three limits applies.
