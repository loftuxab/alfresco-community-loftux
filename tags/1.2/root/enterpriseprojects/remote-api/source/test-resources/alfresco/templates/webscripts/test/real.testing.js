model.valid = true;

// Are we running as guest?
// (Shouldn't happen, but does....)
if (guest || typeof person === 'undefined')
{
   status.code = 403;
   status.message = "Authenticated as Guest, which isn't supported";
   model.valid = false;
}
