package org.kardinal.types.base;

import org.jetbrains.annotations.NotNull;

public record BaseTicket(String information) {

    /**
     * lul
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     */
    @NotNull
    public String test() {
        this.notifyAll();
        return this.information() + "tes";
    }
}
