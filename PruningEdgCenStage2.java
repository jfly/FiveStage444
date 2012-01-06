package fivestage444;

import java.io.File;

public final class PruningEdgCenStage2 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage2_edgcen_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE2_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = (long)(Constants.N_SYMCENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS);
		int n = (int)(num_positions/4 + 1);
		CubeStage2.prune_table_edgcen = new byte[n];
		ptable = CubeStage2.prune_table_edgcen;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i=0; i < Constants.STAGE2_NUM_SOLVED_SYMCENTER_CONFIGS; i++){
			set_dist(Constants.stage2_solved_symcenters[i]*Constants.N_STAGE2_EDGE_CONFIGS + 414, 3);
			System.out.println("solved:"+(Constants.stage2_solved_symcenters[i]*Constants.N_STAGE2_EDGE_CONFIGS + 414));
			int symI = 0;
			int syms = Tables.hasSymCenterSTAGE2[Constants.stage2_solved_symcenters[i]];
			while (syms != 0){
				if(( syms & 0x1 ) == 1 ){
					short edge2 = Tables.move_table_edge_conjSTAGE2[414][symI];
					if (edge2 == 0){
						set_dist (Constants.stage2_solved_symcenters[i]*Constants.N_STAGE2_EDGE_CONFIGS, 3);
						System.out.println("solved:"+(Constants.stage2_solved_symcenters[i]*Constants.N_STAGE2_EDGE_CONFIGS));
						break;
					}
				}
				symI++;
			syms >>= 1;
			}
		}
	}

	long do_move (long idx, int move){
		short edge = (short)(idx % Constants.N_STAGE2_EDGE_CONFIGS);
		short cen = (short)(idx / Constants.N_STAGE2_EDGE_CONFIGS);

		short newCen = Tables.move_table_symCenterSTAGE2[cen][move];
		int sym = newCen & 0xF;
		int cenRep = newCen >> 4;

		edge = Tables.move_table_edgeSTAGE2[edge][move];
		edge = Tables.move_table_edge_conjSTAGE2[edge][sym];
		if((cenRep*Constants.N_STAGE2_EDGE_CONFIGS + edge) == 292903) System.out.println("coming from "+idx+"-edge:"+(idx % Constants.N_STAGE2_EDGE_CONFIGS));
		return cenRep*Constants.N_STAGE2_EDGE_CONFIGS + edge;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		short edge = (short)(idx % Constants.N_STAGE2_EDGE_CONFIGS);
		short cen = (short)(idx / Constants.N_STAGE2_EDGE_CONFIGS);
		int symI = 0;
		int syms = Tables.hasSymCenterSTAGE2[cen];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short edge2 = Tables.move_table_edge_conjSTAGE2[edge][symI];
				set_dist (cen*Constants.N_STAGE2_EDGE_CONFIGS + edge2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
