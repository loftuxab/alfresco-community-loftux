if (document.properties["ask:status"] == "Current") {
  if (logger.isLoggingEnabled()) {
		logger.log("Status is current, applying content hits aspect.");
	}
   if (!document.hasAspect("ch:contentHits")) {   	
    document.addAspect("ch:contentHits");
  }
}

  