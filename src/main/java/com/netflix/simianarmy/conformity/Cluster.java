/*
 *
 *  Copyright 2013 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.simianarmy.conformity;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The class implementing clusters. Cluster is the basic unit of conformity check. It can be a single ASG or
 * a group of ASGs that belong to the same application, for example, a cluster in the Asgard deployment system.
 */
public class Cluster{
    public static final String OWNER_EMAIL = "ownerEmail";
    public static final String CLUSTER = "cluster";
    public static final String REGION = "region";


    public static final String UPDATE_TIMESTAMP = "updateTimestamp";
    public static final String EXCLUDED_RULES = "excludedRules";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String name;
    private final Collection<AutoScalingGroup> autoScalingGroups = Lists.newArrayList();
    private final String region;
    private String ownerEmail;
    private Date updateTime;
    private ClusterConformity clusterConformity = new ClusterConformity();
    
    private final Collection<String> excludedConformityRules = Sets.newHashSet();
    

    private final Set<String> soloInstances = Sets.newHashSet();

    /**
     * Constructor.
     * @param name
     *          the name of the cluster
     * @param autoScalingGroups
     *          the auto scaling groups in the cluster
     */
    public Cluster(String name, String region, AutoScalingGroup... autoScalingGroups) {
        Validate.notNull(name);
        Validate.notNull(region);
        Validate.notNull(autoScalingGroups);
        this.name = name;
        this.region = region;
        for (AutoScalingGroup asg : autoScalingGroups) {
            this.autoScalingGroups.add(asg);
        }
    }

    /**
     * Constructor.
     * @param name
     *          the name of the cluster
     * @param soloInstances
     *          the list of all instances
     */
    public Cluster(String name, String region, Set<String> soloInstances) {
        Validate.notNull(name);
        Validate.notNull(region);
        Validate.notNull(soloInstances);
        this.name = name;
        this.region = region;
        for (String soleInstance : soloInstances) {
            this.soloInstances.add(soleInstance);
        }
    }

    /**
     * Gets the name of the cluster.
     * @return
     *      the name of the cluster
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the region of the cluster.
     * @return
     *      the region of the cluster
     */
    public String getRegion() {
        return region;
    }

    /**
     * * Gets the auto scaling groups of the auto scaling group.
     * @return
     *    the auto scaling groups in the cluster
     */
    public Collection<AutoScalingGroup> getAutoScalingGroups() {
        return Collections.unmodifiableCollection(autoScalingGroups);
    }

    /**
     * Gets the owner email of the cluster.
     * @return
     *      the owner email of the cluster
     */
    public String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * Sets the owner email of the cluster.
     * @param ownerEmail
     *              the owner email of the cluster
     */
    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    /**
     * Gets the update time of the cluster.
     * @return
     *      the update time of the cluster
     */
    public Date getUpdateTime() {
        return new Date(updateTime.getTime());
    }

    /**
     * Sets the update time of the cluster.
     * @param updateTime
     *              the update time of the cluster
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = new Date(updateTime.getTime());
    }



    


    

    /**
     * Gets names of all excluded conformity rules for this cluster.
     * @return
     *      names of all excluded conformity rules for this cluster
     */
    public Collection<String> getExcludedRules() {
        return Collections.unmodifiableCollection(excludedConformityRules);
    }

    /**
     * Excludes rules for the cluster.
     * @param ruleIds
     *          the rule ids to exclude
     * @return
     *          the cluster itself
     */
    public Cluster excludeRules(String... ruleIds) {
        Validate.notNull(ruleIds);
        for (String ruleId : ruleIds) {
            Validate.notNull(ruleId);
            excludedConformityRules.add(ruleId.trim());
        }
        return this;
    }

    

   

    /**
     * Gets a map from fields of resources to corresponding values. Values are represented
     * as Strings so they can be displayed or stored in databases like SimpleDB.
     * @return a map from field name to field value
     */
    public Map<String, String> getFieldToValueMap() {
        Map<String, String> map = Maps.newHashMap();
        putToMapIfNotNull(map, CLUSTER, name);
        putToMapIfNotNull(map, REGION, region);
        putToMapIfNotNull(map, OWNER_EMAIL, ownerEmail);
        putToMapIfNotNull(map, UPDATE_TIMESTAMP, String.valueOf(DATE_FORMATTER.print(updateTime.getTime())));
        putToMapIfNotNull(map, EXCLUDED_RULES, StringUtils.join(excludedConformityRules, ","));
        return map;
    }

    /**
     * Parse a map from field name to value to a cluster.
     * @param fieldToValue the map from field name to value
     * @return the cluster that is de-serialized from the map
     */
    public static Cluster parseFieldToValueMap(Map<String, String> fieldToValue) {
        Validate.notNull(fieldToValue);
        Cluster cluster = new Cluster(fieldToValue.get(CLUSTER),
                fieldToValue.get(REGION));
        cluster.setOwnerEmail(fieldToValue.get(OWNER_EMAIL));   
        cluster.excludeRules(StringUtils.split(fieldToValue.get(EXCLUDED_RULES), ","));
        cluster.setUpdateTime(new Date(DATE_FORMATTER.parseDateTime(fieldToValue.get(UPDATE_TIMESTAMP)).getMillis()));
        
        return cluster;
    }

    private static void putToMapIfNotNull(Map<String, String> map, String key, String value) {
        Validate.notNull(map);
        Validate.notNull(key);
        if (value != null) {
            map.put(key, value);
        }
    }

    public Set<String> getSoloInstances() {
        return Collections.unmodifiableSet(soloInstances);
    }

}
