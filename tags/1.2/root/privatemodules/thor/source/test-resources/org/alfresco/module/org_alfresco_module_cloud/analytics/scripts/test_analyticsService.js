/**
 * This is a trivial test that sends a basic analytics event from this script to ensure the Script API is functional.
 */
function smokeTestAnalyticsScriptApi()
{
    analyticsService.record_UploadDocument("text/plain", 314159, false);
}

smokeTestAnalyticsScriptApi();
