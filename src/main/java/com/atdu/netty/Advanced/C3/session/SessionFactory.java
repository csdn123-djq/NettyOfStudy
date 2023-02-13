package com.atdu.netty.Advanced.C3.session;

public abstract class SessionFactory {

    private static final Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
