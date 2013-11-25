/**
*
* Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Administrative Contact: dnet-project-office@cs.kuleuven.be
* Technical Contact: bart.vanbrabant@cs.kuleuven.be
*/
package gcroes.thesis.docproc.jee.monitoring;

import java.util.List;

public class Statistic {

        private String name;
        private String role = "dev";
        private float cost = 1.0f;

        private double average;
        private double sdtDev;
        private long samples;

        public Statistic(String name, double average, double sdtDev, long samples) {
                super();
                this.name = name;
                this.average = average;
                this.sdtDev = sdtDev;
                this.samples = samples;
        }
        
        @SuppressWarnings("unused")
        private Statistic() {}

        public Statistic(String name, List<Statistic> children) {
                super();
                this.name = name;

                // http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Parallel_algorithm

                double sum = 0;
                long count = 0;
                double moment = 0;

                for (Statistic s : children) {
                        // initialisation is not perfect yet ;-)
                        double delta = count == 0 ? 0 : (s.getAverage() - sum / count);
                        long nx = count + s.getSamples();
                        double mymoment = s.getSdtDev() * s.getSdtDev()
                                        * (s.getSamples() - 1);
                        moment += mymoment + delta * delta * count * s.getSamples() / nx;
                        sum += s.average * s.getSamples();
                        count = nx;

                }

                this.average = sum / count;
                this.samples = count;
                this.sdtDev = Math.sqrt(moment / (count - 1));
        }

        public Statistic(String name, List<Integer> samples, float div) {
                this.name = name;
                this.samples = samples.size();

                int n = samples.size();

                float sum1 = 0, sum2 = 0;
                for (int x : samples) {
                        float xc = x / div;
                        sum1 = sum1 + xc;
                        sum2 = sum2 + xc * xc;
                }

                float mean = sum1 / n;

                this.average = mean;

                this.sdtDev = Math.sqrt((sum2 / n - mean * mean) * n / (n - 1));
        }

        public String getName() {
                return name;
        }

        public double getAverage() {
                return average;
        }

        public double getSdtDev() {
                return sdtDev;
        }

        public long getSamples() {
                return samples;
        }

        public void setName(String name) {
                this.name = name;
        }

        public float getCost() {
                return cost;
        }

        public String getRole() {
                return role;
        }

        public void setAverage(double average) {
                this.average = average;
        }

        public void setSdtDev(double sdtDev) {
                this.sdtDev = sdtDev;
        }

        public void setSamples(long samples) {
                this.samples = samples;
        }

        public void setCost(float cost) {
                this.cost = cost;
        }

        public void setRole(String role) {
                this.role = role;
        }

        @Override
        public String toString() {
                return "Statistic [name=" + name + ", samples=" + samples
                                + ", average=" + average + ", sdtDev=" + sdtDev + "]";
        }

        @Override
        public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((name == null) ? 0 : name.hashCode());
                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (obj == null)
                        return false;
                if (getClass() != obj.getClass())
                        return false;
                Statistic other = (Statistic) obj;
                if (name == null) {
                        if (other.name != null)
                                return false;
                } else if (!name.equals(other.name))
                        return false;
                return true;
        }

}