package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class CallbackBuilder extends Builder {

    private final int timeout;

    protected transient boolean waiting;

    @DataBoundConstructor
    public CallbackBuilder(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        waiting = true;
        CallbackAction callback = new CallbackAction(this);
        build.addAction(callback);
        waitForCallback(listener);
        if (waiting) {
            listener.error("Interrupted by Time out");
            build.setResult(Result.FAILURE);
            return false;
        } else {
            listener.getLogger().println("Resuming build");
        }
        return true;
    }

    private synchronized void waitForCallback(BuildListener listener) throws InterruptedException {
        listener.getLogger().println("Block waiting for callback call ...");
        wait(timeout * 1000);
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Block waiting for callback invocation";
        }
    }
}
