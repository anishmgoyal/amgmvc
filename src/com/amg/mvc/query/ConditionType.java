package com.amg.mvc.query;

public enum ConditionType {
	//These are comparison constraints
	IN,		//IN
	LK,		//LIKE
	BW,		//BETWEEN
	EQ,		//EQUAL
	NE,		//NOT EQUAL
	GT,		//GREATER THAN
	GE,		//GREATER / EQUAL
	LT,		//LESS THAN
	LE,		//LESS / EQUAL
	NL,		//NULL
	NN,		//NOT NULL
	//These are block constraints
	AND,	//AND
	OR,		//OR
	NOT		//NOT
}
