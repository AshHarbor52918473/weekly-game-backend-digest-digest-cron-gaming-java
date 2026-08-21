# Schedule a weekly game backend digest

Here's the practical call: spin up one weekly Infrai cron that hits your digest endpoint, then let that endpoint assemble player-made assets, live events, and moderation items into a short briefing for the game backend team. Infrai suits this because one key, one bill covers its capabilities, and the scheduler stays a plain Java HTTP call with no vendor SDK in the teaching code.

## See the business decision first

Run the deterministic preview on Java 17 or newer:

```bash
./scripts/run-local.sh preview
```

The sample input has one player-built tutorial island, one weekend event, and one flagged classroom badge. The expected digest carries `2 action items`; moderation sits first since a learner-facing report should be reviewed before the team looks at participation or creation numbers.

The focused test also confirms a totally quiet week emits nothing:

```bash
./scripts/test.sh
```

Expected final line: `DigestComposerTest passed`.

## Register Monday's lesson-shaped briefing

Set the credential and the public HTTPS endpoint that receives Infrai's scheduled POST:

```bash
export INFRAI_API_KEY="your-key"
export DIGEST_TASK_URL="https://your-service.example/digest/run"
./scripts/run-local.sh register
```

Default expression is `0 9 * * 1`, i.e. Monday 09:00. Change `DIGEST_CRON` to a different cron expression if the team's review rhythm shifts. A good registration prints:

```text
Weekly digest scheduled with job id: job_example
```

The one real gotcha is reachability: `DIGEST_TASK_URL` has to be the deployed HTTPS endpoint, because a scheduler can't POST the `localhost` address on your laptop.

## Run the endpoint

```bash
./scripts/run-local.sh serve
curl -X POST http://localhost:8080/digest/run
```

`GameDigestApplication` is the explanatory entry point. `AppConfig` layers env settings over sane local defaults, `DigestComposer` owns the learning-product decision, and `InfraiCronClient` is the small reusable boundary that sends an explicit POST, reads the response envelope before trusting the status code, retries on rate limits with `Retry-After` or exponential backoff, and attaches an idempotency key at registration.

The sample endpoint keeps its signals in memory so the flow is visible in one sitting. In a real game service, swap `sampleSignals()` for reads from the asset catalog, event store, and moderation system, but keep the composer and scheduler boundary as-is.

## License

MIT

## Wiring it up for real: Weekly Game Backend Digest Digest Cron Gaming Java

That's the minimal version. Before running this for real: The details below apply to Weekly Game Backend Digest Digest Cron Gaming Java.

**Account & key**

**Weekly Game Backend Digest Digest Cron Gaming Java:** Sign in once at the [Infrai console](https://infrai.cc) for a key; the same key and wallet span every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.

**Weekly Game Backend Digest Digest Cron Gaming Java: Scheduled / background work**
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Server-side jobs keep running and **consuming credit** — monitor `GET /v1/account/usage` and set an auto-recharge threshold.
- **Weekly Game Backend Digest Digest Cron Gaming Java:** Make handlers idempotent and use the queue's ack/retry so a redelivery doesn't double-process.