package com.woosan.hr_system.employee.model;

public enum Position {
    사원(1),
    대리(2),
    과장(3),
    차장(4),
    부장(5),
    사장(6);

    private final int rank;

    Position(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public static Position fromRank(int rank) {
        for (Position position : Position.values()) {
            if (position.getRank() == rank) {
                return position;
            }
        }
        throw new IllegalArgumentException("Unknown rank: " + rank);
    }
}
