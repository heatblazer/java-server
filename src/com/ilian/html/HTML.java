package com.ilian.html;

/**
 * Created by ilian on 9/26/16.
 */
public class HTML {

    private StringBuilder m_string = null;

    public HTML()
    {
        m_string = new StringBuilder();
    }

    @Override
    public String toString()
    {
        return m_string.toString();
    }

}
