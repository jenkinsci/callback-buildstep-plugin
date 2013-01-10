package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.UnprotectedRootAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CallbackAction implements Action {

    private final CallbackBuilder builder;

    public CallbackAction(CallbackBuilder builder) {
        this.builder = builder;
    }

    public void doIndex(StaplerRequest req, StaplerResponse rsp) {
        synchronized (builder) {
            builder.waiting = false;
            builder.notify();
        }
    }

    public String getIconFileName() {
        return "redo.png";
    }

    public String getDisplayName() {
        return "Resume";
    }

    public String getUrlName() {
        return "resume";
    }

}
