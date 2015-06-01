/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.btm.api.model.btxn;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class represents a Correlation Identifier. These identifies are used to
 * link information generated by multiple systems involved in the execution of
 * a business transaction instance.
 *
 * @author gbrown
 *
 */
public class CorrelationIdentifier {

    @JsonInclude
    private String value;

    @JsonInclude
    private Scope scope;

    @JsonInclude(Include.NON_DEFAULT)
    private int duration = 0;

    public CorrelationIdentifier() {
    }

    public CorrelationIdentifier(Scope scope, String value) {
        this.value = value;
        this.scope = scope;
    }

    public CorrelationIdentifier(Scope scope, String value, int duration) {
        this.value = value;
        this.scope = scope;
        this.duration = duration;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the scope
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * This method determines whether the supplied correlation identifier,
     * associated with a base time, matches this correlation identifier
     * associated with its own base time.
     *
     * @param thisBaseTime This correlation id's base time
     * @param cid The correlation id to match against
     * @param cidBaseTime The base time of the correlation id to match
     * @return
     */
    public boolean match(long thisBaseTime, CorrelationIdentifier cid, long cidBaseTime) {

        if (this.equals(cid)) {

            if (getDuration() == 0 && cid.getDuration() == 0) {
                return true;
            } else {
                return isOverlap(thisBaseTime, getDuration(), cidBaseTime, cid.getDuration());
            }
        }

        return false;
    }

    /**
     * This method determines whether there is an overlap between the time periods represented by
     * the two base times and their durations.
     *
     * @param baseTime1 Base time 1
     * @param duration1 Duration 1
     * @param baseTime2 Base time 2
     * @param duration2 Duration 2
     * @return
     */
    protected static boolean isOverlap(long baseTime1, int duration1, long baseTime2, int duration2) {
        long endTime1 = baseTime1 + duration1;
        long endTime2 = baseTime2 + duration2;

        if ((baseTime1 >= baseTime2 && baseTime1 <= endTime2)
                || (baseTime2 >= baseTime1 && baseTime2 <= endTime1)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // NOTE: Duration should not be used as part of hashcode

        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CorrelationIdentifier other = (CorrelationIdentifier) obj;

        // NOTE: Duration should not be evaluated as part of equality,
        // although will be relevant when evaluating whether the correlation
        // identifier is relevant

        if (scope != other.scope) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * This enumerated value represents the scope of the correlation identifier.
     *
     * @author gbrown
     *
     */
    public enum Scope {
        /**
         * The 'Global' scope means the identifier is unique over the end to end
         * life of the business transaction.
         */
        Global,

        /**
         * The 'Local' scope means the identifier is unique within an island
         * of the business transaction (e.g. a business process instance).
         */
        Local,

        /**
         * The 'Exchange' scope means the identifier is only unique in relation
         * to a specific exchange between two (or more) participants.
         */
        Exchange
    }
}