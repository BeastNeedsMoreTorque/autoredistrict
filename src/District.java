//buisness objects
class District {
    Vector<Block> blocks = new Vector<Block>();
    public double[] wins;
    public int last_winner = -1;

    double getPopulation() {
        double pop = 0;
        for( Block block : blocks)
              pop += block.population;
        return pop;
    }

    public void resetWins() {
        if( blocks.size() == 0) {
            return;
        }
        wins = new double[blocks.get(0).prob_vote.length]; //inited to 0
    }
    public double getSelfEntropy() {
        double total = 0;
        for( int i = 0; i < wins.length; i++) {
            total += wins[i];
        }

        double H = 0;
        for( int i = 0; i < wins.length; i++) {
            double p = ((double)wins[i]) / total; 
            H -= p*Math.log(p);
        }

        return H;
    }
    public static int getHighestIndex(double[] dd) {
        double most_value = 0;
        int most_index = 0;
        for( int i = 0; i < dd.length; i++) {
            if( dd[i] > most_value) {
                most_index = i;
                most_value = dd[i];//district_vote[i];
            }
        }
        return most_index;
    }

    public double[] getVotes() {
        if( blocks.size() == 0) {
            return null;
        }
        double[] district_vote = new double[blocks.get(0).prob_vote.length]; //inited to 0
        for( Block block : blocks) {
            double[] block_vote = block.getVotes();
            for( int i = 0; i < block_vote.length; i++) {//most_value) {
                district_vote[i] += block_vote[i];
            }
        }
        last_winner = getHighestIndex(district_vote);
        wins[last_winner]++;

        return district_vote;
    }

    //getRegionCount() counts the number of contiguous regions by counting the number of vertex cycles.  a proper map will have exactly 1 contiguous region per district.
    //this is a constraint to apply _AFTER_ a long initial optimization.  as a final tuning step.
    int getRegionCount(int[] block_districts) {
        return getRegions(block_districts).size();
    }

    Vector<Block> getTopPopulationRegion(int[] block_districts) {
        Vector<Vector<Block>> regions = getRegions(block_districts);
        Vector<Block> high = null;
        double max_pop = 0;
        for( Vector<Block> region : regions) {
            double pop = getRegionPopulation(region);
            if( pop > max_pop || high == null) {
                max_pop = pop;
                high = region;
            }
        }
        return high;
    }
    Vector<Vector<Block>> getRegions(int[] block_districts) {
        Hashtable<Block,Vector<Block>> region_hash = new Hashtable<Block,Vector<Block>>();
        Vector<Vector<Block>> regions = new Vector<Vector<Block>>();
        for( Block block : blocks) {
            if( region_hash.get(block) != null)
                continue;
            Vector<Block> region = new Vector<Block>();
            regions.add(region);
            addAllConnected(block,region,region_hash,block_districts);
        }
        return regions;
    }
    //recursively insert connected blocks.
    void addAllConnected( Block block, Vector<Block> region,  Hashtable<Block,Vector<Block>> region_hash, int[] block_districts) {
        if( region_hash.get(block) != null)
            return;
        region.add(block);
        region_hash.put(block,region);
        for( Edge edge : block.edges)
            if( edge.areBothSidesSameDistrict(block_districts))
                addAllConnected( edge.block1 == block ? edge.block2 : edge.block1, region, region_hash, block_districts);
    }
    double getRegionPopulation(Vector<Block> region) {
        double population = 0;
        for( Block block : region)
            population += block.population;
        return population;
    }


}
class Block {
    int index;
    double[] population;
    double[] prob_turnout;
    double[][] prob_vote = null;//new double[DistrictMap.candidates.size()];
    double[] vote_cache = null;
    double[][] vote_caches = null;
    static boolean use_vote_caches = true;
    static boolean use_vote_cache = true;
    static int cache_reuse_times = 16;
    static int vote_cache_size = 128;
    int cache_reused = 0;

    Vector<Edge> edges = new Vector<Edge>();
    double[] getVotes() {
        if( use_vote_caches) {
            if( vote_caches == null) {
                vote_caches = new double[vote_cache_size][];
                for( int i = 0; i < vote_caches.length; i++) {
                    generateVotes();
                    vote_caches[i] = vote_cache;
                }
            }
            return vote_caches[((int)Math.random()*(double)vote_caches.length)];
        } else if( vote_cache == null || cache_reused >= cache_reuse_times) {
            generateVotes();
            cache_reuse_times = 0;
        }
        cache_reuse_times++;
        return vote_cache;
    }

    void generateVotes() {
        double[] votes = new double[prob_vote.length];
        for(int i = 0; i < votes; i++) {
            votes[i] = 0;
        }
        for(int h = 0; h < population.length; h++) {
            for(int i = 0; i < population[h]; i++) {
                double p = Math.random();
                if( p > prob_turnout[h]) {
                    continue;
                }
                p = Math.random();
                for( int j = 0; j < prob_vote[h].length; j++) {
                    p -= prob_vote[h][j];
                    if( p <= 0) {
                        votes[j]++;
                        break;
                    }
                }
            }
        }
    vote_cache = votes;
    }
}