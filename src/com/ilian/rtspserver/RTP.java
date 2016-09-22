package com.ilian.rtspserver;


/**
 * Created by ilian on 9/20/16.
 */
public class RTP
{
    /* this is the final rtp string */
    private  StringBuilder m_rtp_string = null;

    public RTP(/* what to do in the ctor?*/)
    {
        m_rtp_string = new StringBuilder();
    }

    public void options(String opts)
    {
        m_rtp_string.append("\nRTSP: OPTIONS ");
        m_rtp_string.append(opts);
        m_rtp_string.append("\n");
    }

    public void describe(String opts)
    {
        m_rtp_string.append("\nRTSP: DESCRIBE ");
        m_rtp_string.append(opts);
        m_rtp_string.append("\n");
    }

    public void setup(String opts)
    {
        m_rtp_string.append("\nRTSP: SETUP ");
        m_rtp_string.append(opts);
        m_rtp_string.append("\n");
    }

    public void play(String opts)
    {
        m_rtp_string.append("\nRTSP: PLAY ");
        m_rtp_string.append(opts);
        m_rtp_string.append("\n");
    }

    public void teardown(String opts)
    {
        m_rtp_string.append("\nRTSP: TEARDOWN ");
        m_rtp_string.append(opts);
        m_rtp_string.append("\n");
    }

    @Override
    public String toString()
    {
        return m_rtp_string.toString();
    }

}
