package emu.lunarcore.util;

import dev.morphia.annotations.Entity;
import emu.lunarcore.proto.VectorOuterClass.Vector;

@Entity(useDiscriminator = false)
public class Position {
    private int x;
    private int y;
    private int z;

    public Position() {

    }

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Position position) {
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
    }
    
    public Position(Vector vector) {
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void set(Position pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    // Operations

    public double get2dDist(Position pos) {
        int x = this.getX() - pos.getX();
        int z = this.getZ() - pos.getZ();
        return Math.sqrt((x * x) + (z * z));
    }

    public long getFast2dDist(Position pos) {
        long x = this.getX() - pos.getX();
        long z = this.getZ() - pos.getZ();
        return (x * x) + (z * z);
    }

    public Vector toProto() {
        return Vector.newInstance().setX(x).setY(y).setZ(z);
    }

    // Overrides

    @Override
    public Position clone() {
        return new Position(getX(), getY(), getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position pos) {
            return getX() == pos.getX() && getY() == pos.getY() && getZ() == pos.getZ();
        }
        return false;
    }

    @Override
    public String toString() {
        return "[ " + this.getX() + " , " + this.getY() + " ]";
    }
}
