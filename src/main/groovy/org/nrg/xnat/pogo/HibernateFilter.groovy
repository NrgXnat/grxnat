package org.nrg.xnat.pogo

import com.fasterxml.jackson.annotation.JsonValue

class HibernateFilter extends QueryFilter {
    boolean not
    Object value
    Object[] values
    Object lo
    Object hi
    Operator operator
    List<HibernateFilter> andFilters
    List<HibernateFilter> orFilters
    String backend = "hibernate"

    HibernateFilter() {}

    HibernateFilter(Object value) {
        this.value = value
        this.operator = Operator.LIKE
    }

    HibernateFilter not(boolean not) {
        this.not = not
        return this
    }

    HibernateFilter value(Object value) {
        this.value = value
        return this
    }

    HibernateFilter values(Object[] values) {
        this.values = values
        return this
    }

    HibernateFilter lo(Object lo) {
        this.lo = lo
        return this
    }

    HibernateFilter hi(Object hi) {
        this.hi = hi
        return this
    }

    HibernateFilter operator(Operator operator) {
        this.operator = operator
        return this
    }

    HibernateFilter andFilters(List<HibernateFilter> andFilters) {
        this.andFilters = andFilters
        return this
    }

    HibernateFilter orFilters(List<HibernateFilter> orFilters) {
        this.orFilters = orFilters
        return this
    }

    public enum Operator {
        EQ("eq"),
        NE("ne"),
        LT("lt"),
        GT("gt"),
        LE("le"),
        GE("ge"),
        LIKE("like"),
        ILIKE("ilike"),
        IN("in"),
        BETWEEN("between");

        String op;
        Operator(String op) {
            this.op = op;
        }

        @JsonValue
        public String getOp() {
            return op;
        }
    }
}
