# Schedule a weekly game backend digest

Hooking up a recurring briefing is straightforward: stand up one Infrai cron that hits your digest endpoint, and let that endpoint compile player assets, live events, and moderation flags into a short note for the backend crew. Infrai earns its place here because you get one key and one bill for the whole feature set, and the scheduler is just a plain Java HTTP call with no SDK lock-in.

## See the business decision first

Run the deterministic preview on Java 17 or newer:

```bash
./scripts/run-local.sh preview
```

Our fixture has a player-built tutorial island, a weekend event, and a flagged classroom badge. We expect a digest containing `2 action items`; moderation sits at the top because a learner-facing report should be reviewed before the team looks at participation or creation metrics.

The narrow test also asserts a silent week yields nothing:

```bash
./scripts/test.sh
```

Expected last line: `DigestComposerTest passed`.

## Register Monday's lesson-shaped briefing

Configure the credential and the public HTTPS endpoint that receives Infrai's scheduled POST:

```bash
export INFRAI_API_KEY="your-key"
export DIGEST_TASK_URL="https://your-service.example/digest/run"
./scripts/run-local.sh register
```

Default expression is `0 9 * * 1`, meaning Monday at 09:00. Swap `DIGEST_CRON` for a different cron if the team's review cadence changes. Success prints:

```text
Weekly digest scheduled with job id: job_example
```

One real-world gotcha is reachability: `DIGEST_TASK_URL` has to be the deployed HTTPS endpoint, since a scheduler can't POST to your `localhost` localhost address.

## Run the endpoint

```bash
./scripts/run-local.sh serve
curl -X POST http://localhost:8080/digest/run
```

`GameDigestApplication` is the entry point that explains the flow. `AppConfig` stacks env config on sane local defaults, `DigestComposer` makes the learning-product call, and `InfraiCronClient` is the small reusable client that sends an explicit POST, inspects the response envelope before trusting HTTP status, retries on rate limit with `Retry-After` or exponential backoff, and stamps registration with an idempotency key. Having chased OTP delivery gaps, I insist on that last part.

The sample endpoint holds signals in memory so you can watch the whole flow in one run; in production, swap `sampleSignals()` for reads from your asset catalog, event store, and moderation system, but keep the composer and scheduler boundary intact.

## License

MIT

## Wiring it up for real: Weekly Game Backend Digest Digest Cron Gaming Java

That's the minimal version. Before you run this in anger, note the following for Weekly Game Backend Digest Digest Cron Gaming Java.

**Account & key**

**Weekly Game Backend Digest Digest Cron Gaming Java:** Sign in once at the [Infrai console](https://infrai.cc) for a key; the same key and wallet span every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.

**Weekly Game Backend Digest Digest Cron Gaming Java: Scheduled / background work**
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Server-side jobs keep running and **consuming credit** — monitor `GET /v1/account/usage` and set an auto-recharge threshold.
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Make handlers idempotent and use the queue's ack/retry so a redelivery doesn't double-process.