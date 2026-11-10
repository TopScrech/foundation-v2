package asmodeuscore.core.network.packet;

import asmodeuscore.api.network.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class PacketBase implements IMessage
{
    public static final int INVALID_DIMENSION_ID = Integer.MIN_VALUE + 12;
    private int dimensionID;

    public PacketBase()
    {
        this.dimensionID = INVALID_DIMENSION_ID;
    }

    public PacketBase(int dimensionID)
    {
        this.dimensionID = dimensionID;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        if (dimensionID == INVALID_DIMENSION_ID)
        {
            throw new IllegalStateException("Invalid Dimension ID! [AC]");
        }
        buffer.writeInt(this.dimensionID);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        this.dimensionID = buffer.readInt();
    }

    @Override
    public int getDimensionID()
    {
        return dimensionID;
    }
}
