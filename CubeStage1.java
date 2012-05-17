package fivestage444;

public final class CubeStage1 {

	public short m_co; // corner orientation (2187)
	public int m_sym_edge_ud_combo8; // (46371)

	public static PruningStage1 prune_table;

	public void copyTo (CubeStage1 cube1){
		cube1.m_co = m_co;
		cube1.m_sym_edge_ud_combo8 = m_sym_edge_ud_combo8;
	}

	public void do_move (int move_code){
		int fmc = Constants.basic_to_face[move_code];
		if (fmc >= 0)
			m_co = Tables.move_table_co[m_co][fmc];

		int sym = m_sym_edge_ud_combo8 & 0x3F;
		int rep = m_sym_edge_ud_combo8 >> 6;

		int moveConj = Symmetry.moveConjugate[move_code][sym];
		int newEdge = Tables.move_table_symEdgeSTAGE1[rep][moveConj];

		int newSym = newEdge & 0x3F;
		int newRep = newEdge >> 6;

		m_sym_edge_ud_combo8 = ( newRep << 6 ) + Symmetry.symIdxMultiply[newSym][sym];
	}

	public boolean is_solved (){
		if (( m_sym_edge_ud_combo8 >> 6 ) == 0 && Tables.move_table_co_conj[m_co][m_sym_edge_ud_combo8 & 0x3F] == 1906)
			return true;
		return false;
	}

	/* Convert functions */

	public void convert_edges_to_std_cube (int edge, CubeState result_cube)
	{
		int i;

		int ebm = Tables.eloc2ebm[edge];
		byte lrfb = 0;
		byte ud = 16;
		for (i = 0; i < 24; ++i) {
			if ((ebm & (1 << i)) == 0) {
				result_cube.m_edge[i] = lrfb++;
			} else {
				result_cube.m_edge[i] = ud++;
			}
		}
	}

	public void convert_corners_to_std_cube (CubeState result_cube)
	{
		int i;

		int orientc = m_co;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
			byte fo = (byte)(orientc % 3);
			result_cube.m_cor[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		result_cube.m_cor[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	/* Pruning functions */

	private int get_idx (){
		return Constants.N_CORNER_ORIENT * (m_sym_edge_ud_combo8 >> 6 ) + Tables.move_table_co_conj[m_co][m_sym_edge_ud_combo8 & 0x3F];
	}

	public int get_dist (){
		return prune_table.get_dist_packed(get_idx());
	}

	public int new_dist (int dist){
		return prune_table.new_dist(get_idx(), dist);
	}

	public int getDistance (){
		CubeStage1 cube1 = new CubeStage1();
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, j, dist1, dist2;
		int nDist = 0;

		copyTo (cube1);
		dist1 = cube1.get_dist();

		while( ! cube1.is_solved ()) {

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_BASIC_MOVES; ++mov_idx) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist();
				if (((dist2+1) % 3) != dist1) continue; // If distance is not lowered by 1, continue.
				cube2.copyTo (cube1);
				nDist++;
				dist1 = dist2;
				noMoves=false;
				break;
			}
			if( noMoves){
				System.out.println("Could not find a move that lowers the distance !!");
				break;

			}
		}
		return nDist;
	}
}
