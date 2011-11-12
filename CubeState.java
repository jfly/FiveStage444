package fivestage444;

//CubeState structure: a cubie-level representation of the cube.
public class CubeState {

	Face m_edge[24];	//what's at each edge position
	Face m_cor[8];		//what's at each corner position (3*cubie + orientation)
	Face m_cen[24];		//what's at each center position

	static int rotateCOR_ft[6*6] = {
		 0,  3,  2,  1,  0,  3,	//U face
		 4,  5,  6,  7,  4,  5,	//D face
	     3,  0,  4,  7,  3,  0,	//L face
	     1,  2,  6,  5,  1,  2,	//R face
	     0,  1,  5,  4,  0,  1,	//F face
	     2,  3,  7,  6,  2,  3	//B face
	};
	static int rotateCOR_ori[4] = { 1, 2, 1, 2 };
	static int rotateCOR_fidx[18] = {  0,  2,  0,  6,  8,  6, 12, 14, 12, 18, 20, 18, 24, 26, 24, 30, 32, 30 };
	static int rotateCOR_tidx[18] = {  1,  1,  2,  7,  7,  8, 13, 13, 14, 19, 19, 20, 25, 25, 26, 31, 31, 32 };

	static int rotateEDGE_fidx[18*3] = {
		 0,  1,  0,  6,  7,  6, 12, 13, 12, 18, 19, 18, 24, 25, 24, 30, 31, 30,
		36, 37, 36, 42, 43, 42, 48, 49, 48, 54, 55, 54, 60, 61, 60, 66, 67, 66,
		72, 73, 72, 78, 79, 78, 84, 85, 84, 90, 91, 90, 96, 97, 96,102,103,102
	};
	static int rotateEDGE_tidx[18*3] = {
		 1,  0,  2,  7,  6,  8, 13, 12, 14, 19, 18, 20, 25, 24, 26, 31, 30, 32,
   		37, 36, 38, 43, 42, 44, 49, 48, 50, 55, 54, 56, 61, 60, 62, 67, 66, 68,
		73, 72, 74, 79, 78, 80, 85, 84, 86, 91, 90, 92, 97, 96, 98,103,102,104
	};
	static int rotateEDGE_ft[18*6] = {
		 0, 12,  1, 14,  0, 12, //up face, set 1
	     4,  8,  5, 10,  4,  8, //up face, set 2
		16, 22, 19, 21, 16, 22, //up slice

		 2, 15,  3, 13,  2, 15, //down face, set 1
	     6, 11,  7,  9,  6, 11, //down face, set 2
	    17, 23, 18, 20, 17, 23, //down slice

		 8, 20,  9, 22,  8, 20, //left face, set 1
		12, 16, 13, 18, 12, 16, //left face, set 2
		 0,  6,  3,  5,  0,  6, //left slice

		10, 23, 11, 21, 10, 23, //right face, set 1
		14, 19, 15, 17, 14, 19, //right face, set 2
		 1,  7,  2,  4,  1,  7, //right slice

		 0, 21,  2, 20,  0, 21, //front face, set 1
		 4, 17,  6, 16,  4, 17, //front face, set 2
		 8, 14, 11, 13,  8, 14, //front slice

		 1, 22,  3, 23,  1, 22, //back face, set 1
		 5, 18,  7, 19,  5, 18, //back face, set 2
		 9, 15, 10, 12,  9, 15  //back slice
	};

	static int rotateCEN_ft[18*6] = {
		 0,  3,  1,  2,  0,  3, //up face
	    16, 10, 21, 14, 16, 10, //up slice, set1
		19,  8, 22, 12, 19,  8, //up slice, set2

		 4,  7,  5,  6,  4,  7, //down face
	    18, 13, 23,  9, 18, 13, //down slice, set 1
	    17, 15, 20, 11, 17, 15, //down slice, set 2

		 8, 11,  9, 10,  8, 11, //left face
		16,  6, 20,  3, 16,  6, //left slice, set 1
		18,  5, 22,  0, 18,  5, //left slice, set 2

		12, 15, 13, 14, 12, 15, //right face
		19,  1, 23,  4, 19,  1, //right slice, set 1
		17,  2, 21,  7, 17,  2, //right slice, set 2

		16, 19, 17, 18, 16, 19, //front face
		 0, 14,  4, 11,  0, 14, //front slice, set 1
		 2, 13,  6,  8,  2, 13, //front slice, set 2

		20, 23, 21, 22, 20, 23, //back face
		 1, 10,  5, 15,  1, 10, //back slice, set 1
		 3,  9,  7, 12,  3,  9  //back slice, set 2
	};


	public void init (){
		int i;
		for (i = 0; i < 24; ++i) {
			m_edge[i] = i;
		}
		for (i = 0; i < 8; ++i) {
			m_cor[i] = i;
		}
		for (i = 0; i < 24; ++i) {
			m_cen[i] = i/4;
		}
	}

	public void do_move (int move_code){
		rotate_sliceEDGE (move_code);
		rotate_sliceCORNER (move_code);
		rotate_sliceCENTER (move_code);
	}

	public void compose_edge (CubeState cs1, CubeState cs2){
		int i;
		// *this = cs1; // Is this necessary ? 
		for (i = 0; i < 24; ++i)
			m_edge[i] = cs1.m_edge[cs2.m_edge[i]];
	}

	public void invert_fbcen (){
		int i;
		for (i = 0; i < 24; ++i) {
			if (m_cen[i] >= 4) {
				m_cen[i] ^= 1;
			}
		}
	}

	public boolean edgeUD_parity_odd (){
		int i, j;
		int parity = 0;
		Face t[16];

		for (i = 0; i < 16; ++i) {
			t[i] = m_edge[i];
		}
		for (i = 0; i < 15; ++i) {
			if (t[i] == i) {
				continue;
			}
			for (j = i + 1; j < 16; ++j) {
				if (t[j] == i) {
					//"swap" the i & j elements, but don't bother updating the "i"-element
					//as it isn't needed anymore.
					t[j] = t[i];
				}
			}
			parity ^= 1;
		}
		return parity != 0;
	}

	public void rotate_sliceCORNER (int move_code){
		int i;
		if (move_code % 6 >= 3) {
			return;		//inner slice turn, no corners affected
		}
		int mc6 = move_code/6;
		int mc = 3*mc6 + move_code % 3;
		int fidx = rotateCOR_fidx[mc];
		int tidx = rotateCOR_tidx[mc];
		Face old_m_cor[8]; // FIXME.
		for (i = 0; i < 8; ++i) { // Add. May be faster ?
			old_m_cor[i] = m_cor[i];
		}
		if (mc % 3 != 2) {	//avoid doing "if" inside loop, for speed
			for (i = 0; i < 4; ++i) {
				Face tmpface = old_m_cor[rotateCOR_ft[fidx + i]];
				if (mc >= 6) {	//L,R,F,B face turns
					Face new_ori = (tmpface >> 3) + rotateCOR_ori[i];
					new_ori %= 3;
					tmpface = (tmpface & 0x7) + (new_ori << 3);
				}
				m_cor[rotateCOR_ft[tidx + i]] = tmpface;
			}
		} else {
			for (i = 0; i < 4; ++i) {
				m_cor[rotateCOR_ft[tidx + i]] = old_m_cor[rotateCOR_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceEDGE (int move_code){
		Face old_m_edge[24]; // FIXME.
		for (i = 0; i < 24; ++i) { // Add. May be faster ?
			old_m_edge[i] = m_edge[i];
		}
		int i;
		int mc3 = move_code/3;
		int movdir = move_code % 3;
		int mcx = 3*(mc3/2);
		if ((mc3 & 0x1) != 0) {	//slice move?
			mcx += 2;
		}
		int fidx = rotateEDGE_fidx[3*mcx + movdir];
		int tidx = rotateEDGE_tidx[3*mcx + movdir];
		for (i = 0; i < 4; ++i) {
			m_edge[rotateEDGE_ft[tidx + i]] = old_m_edge[rotateEDGE_ft[fidx + i]];
		}
		if ((mc3 & 0x1) == 0) {	//face move? have a 2nd set of edges to cycle
			fidx = rotateEDGE_fidx[3*(mcx+1) + movdir];
			tidx = rotateEDGE_tidx[3*(mcx+1) + movdir];
			for (i = 0; i < 4; ++i) {
				m_edge[rotateEDGE_ft[tidx + i]] = old_m_edge[rotateEDGE_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceCENTER (int move_code){
		Face old_m_cen[24]; // FIXME.
		for (i = 0; i < 24; ++i) { // Add. May be faster ?
			old_m_cen[i] = m_cen[i];
		}
		int i;
		int mc3 = move_code/3;
		int movdir = move_code % 3;
		int mcx = 3*(mc3/2) + (mc3 & 0x1);
		int fidx = rotateEDGE_fidx[3*mcx + movdir]; // rotateCEN_fidx = rotateEDGE_fidx
		int tidx = rotateEDGE_tidx[3*mcx + movdir]; // rotateCEN_tidx = rotateEDGE_tidx
		for (i = 0; i < 4; ++i) {
			m_cen[rotateCEN_ft[tidx + i]] = old_m_cen[rotateCEN_ft[fidx + i]];
		}
		if ((mc3 & 0x1) == 1) {	//slice move? have a 2nd set of centers to cycle
			fidx = rotateEDGE_fidx[3*(mcx+1) + movdir]; // idem
			tidx = rotateEDGE_tidx[3*(mcx+1) + movdir]; // idem
			for (i = 0; i < 4; ++i) {
				m_cen[rotateCEN_ft[tidx + i]] = old_m_cen[rotateCEN_ft[fidx + i]];
			}
		}
	}

	public void convert_to_stage1 (CubeStage1 result_cube){
		int i;
		int ebm = 0;
		for (i = 0; i < 24; ++i) {
			if (m_edge[i] >= 16) {
				ebm |= (1 << i);
			}
		}
		result_cube.m_edge_ud_combo8 = Tables.ebm2eloc[ebm];
		int orientc = 0;
		for (i = 0; i < 7; ++i) {	//don't want 8th edge orientation
			orientc = 3*orientc + (m_cor[i] >> 3);
		}
		result_cube.m_co = orientc;
	}

	public void convert_to_stage2 (CubeStage2 result_cube){
		int i;
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 0;
		for (i = 0; i < 24; ++i) {
			if (m_cen[i] >= 4) {
				cenbm |= (1 << i);
				if (m_cen[i] == 4) {
					cenbm4of8 |= (1 << j);
				}
				++j;
			}
		}
		result_cube.m_centerFB = 70*Tables.ebm2eloc[cenbm] + Tables.bm4of8_to_70[cenbm4of8];
		int u = perm_n_pack (8, m_edge, 16);
		result_cube.m_edge = Tables.perm_to_420[u];
	}

	public void convert_to_stage3 (const CubeState& init_cube, CubeStage3* result_cube){
		int i;
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 0;
		for (i = 0; i < 16; ++i) {
			if (m_cen[i] >= 4) { // TODO: Remove this.
				printf ("error: cube state not a stage3 position\n");
				exit (1);
			}
			if (m_cen[i] >= 2) {
				cenbm |= (1 << i);
				if (m_cen[i] == 2) {
					cenbm4of8 |= (1 << j);
				}
				++j;
			}
		}
		result_cube.m_centerLR = 70*Tables.e16bm2eloc[cenbm] + Tables.bm4of8_to_70[cenbm4of8];
		int edge_bm = 0;
		for (i = 0; i < 16; ++i) {
			if (m_edge[i] >= 16) { // TODO: Remove this.
				printf ("error: cube state not a stage3 position\n");
				exit (1);
			}
			if (m_edge[i] < 4 || m_edge[i] >= 12) {
				edge_bm |= (1 << i);
			}
		}
		result_cube.m_edge = Tables.e16bm2eloc[edge_bm];
	}

	public void convert_to_stage4 (CubeStage4 result_cube){
		int i;
		Face t6[8];
		//Note: for corners, use of perm_to_420 array requires "squares" style mapping.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		static Face std_to_sqs[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		int edge = cube_state_to_lrfb ();
		int edgerep = Tables.lrfb_get_edge_rep (edge);
		int hash_idx = Tables.stage4_edge_table_lookup (edgerep);
		result_cube.m_edge = Tables.stage4_edge_hash_table_idx[hash_idx];
		for (i = 0; i < 8; ++i) {
			t6[std_to_sqs[i]] = std_to_sqs[m_cor[i]];
		}
		int u = perm_n_pack (8, t6, 0);
		result_cube.m_corner = Tables.perm_to_420[u];
		int cenbm4of8 = 0;
		for (i = 0; i < 8; ++i) {
			if (m_cen[i] >= 2) { // TODO: Remove this.
				printf ("error: cube state not a stage4 position\n");
				exit (1);
			}
			if (m_cen[i] == 0) {
				cenbm4of8 |= (1 << i);
			}
		}
		result_cube.m_centerUD = Tables.bm4of8_to_70[cenbm4of8];
	}

	public int cube_state_to_lrfb (){
		Face t[8];
		set_a_to_array8 (t);
		int u1 = perm_n_pack (8, t, 0);
		set_b_to_array8 (t);
		int u2 = perm_n_pack (8, t, 0);
		return 40320*u2 + u1;
	}

	public void set_a_to_array8 (Face[] t){
		int i;
		int j = 0;
		for (i = 0; i < 8; ++i) {
			if (i >= 4) {
				j = i + 8;
			} else {
				j = i;
			}
			Face t1 = m_edge[j];
			if (t1 >= 4) {
				if (t1 >= 12) {
					t1 -= 8;
				} else { // TODO: Remove this.
					printf ("error: set_a_to_packed8\n");
					exit (1);
				}
			}
			t[i] = t1;
		}
	}

	public void set_b_to_array8 (Face[] t){
		int i;
		for (i = 0; i < 8; ++i) {
			t[i] = m_edge[4 + i] - 4;
		}
	}

	public void convert_std_cube_to_squares (CubeSqsCoord result_cube){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		static Face std_to_sqs_cor[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		static Face std_to_sqs_cen[24] = {
			0,  3,  1,  2,  5,  6,  4,  7,
			8, 11,  9, 10, 13, 14, 12, 15,
		   16, 19, 17, 18, 21, 22, 20, 23
		};
		Face old_m_cor[8]; // FIXME.
		for (i = 0; i < 8; ++i) { // Add. May be faster ?
			old_m_cor[i] = m_cor[i];
		}
		Face old_m_cen[24]; // FIXME.
		for (i = 0; i < 24; ++i) { // Add. May be faster ?
			old_m_cen[i] = m_cen[i];
		}
		for (i = 0; i < 8; ++i) {
			m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[old_m_cor[i]];
		}
		for (i = 0; i < 24; ++i) {
			m_cen[std_to_sqs_cen[i]] = std_to_sqs_cen[4*old_m_cen[i]]/4;
		}
		pack_cubeSQS (result_cube);
	}

	public void pack_cubeSQS (CubeSqsCoord result_cube){
		int ep1 = Constants.perm_n_pack (4, m_edge, 0);
		int ep2 = Constants.perm_n_pack (4, m_edge, 8);
		int ep3 = Constants.perm_n_pack (4, m_edge, 16);
		result_cube.m_ep96x96x96 = 96*96*(4*ep3 + (m_edge[20] - 20)) + 96*(4*ep2 + (m_edge[12] - 12)) +
			4*ep1 + (m_edge[4] - 4);
		result_cube.m_cp96 = 4*Constants.perm_n_pack (4, m_cor, 0) + (m_cor[4] - 4);
		result_cube.m_cen12x12x12 = squares_pack_centers ();
	}

	public int squares_pack_centers (){
		int i;
		int x = 0;
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			if ((m_cen[i] & 0x1) != 0) {
				x |= b;
			}
			b >>= 1;
		}
		int cen1 = Tables.squares_cen_revmap[(x >> 16) & 0xFF];
		int cen2 = Tables.squares_cen_revmap[(x >> 8) & 0xFF];
		int cen3 = Tables.squares_cen_revmap[x & 0xFF];
		return cen1 + 12*cen2 + 12*12*cen3;
	}
}
