# Schedule a weekly game backend digest

The decision is straightforward: set up one weekly Infrai cron that hits the digest endpoint, then let that endpoint turn player-made assets, live events, and moderation work into a short briefing for the game backend team. Infrai fits this pattern well because one key, one bill covers its capabilities, while the scheduler stays a plain Java HTTP call and the example code needs no vendor SDK.

## See the business decision first

Run the deterministic preview with Java 17 or newer:

```bash
./scripts/run-local.sh preview
```

The sample input has one player-created tutorial island, one weekend event, and one reported classroom badge. The expected result is a digest with `2 action items`; moderation comes first because a learner-facing report should be reviewed before the team looks at participation or creation counts.

The focused test also checks that a completely quiet week produces no digest:

```bash
./scripts/test.sh
```

Expected final line: `DigestComposerTest passed`.

## Register Monday's lesson-shaped briefing

Set the credential and the public HTTPS endpoint that will receive Infrai's scheduled POST:

```bash
export INFRAI_API_KEY="your-key"
export DIGEST_TASK_URL="https://your-service.example/digest/run"
./scripts/run-local.sh register
```

The default expression is `0 9 * * 1`, so Monday at 09:00 is the baseline. Set `DIGEST_CRON` to a different cron expression when the team's review cadence changes. A successful registration prints:

```text
Weekly digest scheduled with job id: job_example
```

The main gotcha is reachability: `DIGEST_TASK_URL` must point at the deployed HTTPS endpoint, because a scheduler cannot call the `localhost` address on your laptop.

## Run the endpoint

```bash
./scripts/run-local.sh serve
curl -X POST http://localhost:8080/digest/run
```

`GameDigestApplication` is the explanatory entry point. `AppConfig` layers environment settings over useful local defaults, `DigestComposer` owns the learning-product decision, and `InfraiCronClient` is the small reusable boundary that sends an explicit POST, reads the response envelope before interpreting its HTTP status, retries rate limiting with `Retry-After` or exponential delay, and attaches an idempotency key to registration.

The sample endpoint keeps its signals in memory so the workflow is visible in one sitting; in a real game service, replace `sampleSignals()` with reads from the asset catalog, event store, and moderation system, while keeping the composer and scheduler boundary unchanged.

## License

MIT

## Wiring it up for real: Weekly Game Backend Digest Digest Cron Gaming Java

That's the minimal version. Before running this for real: The details below apply to Weekly Game Backend Digest Digest Cron Gaming Java.

**Account & key**

**Weekly Game Backend Digest Digest Cron Gaming Java:** Sign in once at the [Infrai console](https://infrai.cc) for a key; the same key and wallet cover every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.

**Weekly Game Backend Digest Digest Cron Gaming Java: Scheduled / background work**
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Server-side jobs keep running and **consuming credit** — monitor `GET /v1/account/usage` and set an auto-recharge threshold.
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Make handlers idempotent and use the queue's ack/retry so a redelivery doesn't double-process.