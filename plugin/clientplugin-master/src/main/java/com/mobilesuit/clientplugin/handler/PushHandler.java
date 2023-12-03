package com.mobilesuit.clientplugin.handler;

import com.intellij.dvcs.push.PrePushHandler;
import com.intellij.dvcs.push.PushInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.mobilesuit.clientplugin.client.SocketClient;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PushHandler implements PrePushHandler {
    private final SocketClient socketClient = SocketClient.getInstance();
    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getPresentableName() {
        return "PushMessageHandler";
    }

    @Override
    public @NotNull Result handle(@NotNull List<PushInfo> pushDetails, @NotNull ProgressIndicator indicator) {
        for(PushInfo pushInfo : pushDetails) {
            socketClient.sendPushEvent(pushInfo.getPushSpec(),pushInfo.getRepository(),pushInfo.getCommits());
            System.out.println(pushInfo.getPushSpec());
            System.out.println(pushInfo.getRepository());
        }
        return Result.OK;
    }
}
